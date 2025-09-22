package com.example.emobile.linkshorteningservice.service.impl;

import com.example.emobile.linkshorteningservice.dto.LinkRedirectDto;
import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.exception.DuplicateAliasException;
import com.example.emobile.linkshorteningservice.exception.LinkNotFoundException;
import com.example.emobile.linkshorteningservice.mapper.LinkMapper;
import com.example.emobile.linkshorteningservice.repository.ReactiveLinkRepository;
import com.example.emobile.linkshorteningservice.service.LinkService;
import com.example.emobile.linkshorteningservice.service.generator.KeyGenerator;
import com.example.emobile.linkshorteningservice.service.validator.CompositeLinkValidator;
import com.example.emobile.linkshorteningservice.service.validator.UniqueAliasValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkMapper linkMapper;
    private final KeyGenerator keyGenerator;
    private final UniqueAliasValidator uniqueAliasValidator;
    private final CompositeLinkValidator compositeLinkValidator;
    private final ReactiveLinkRepository reactiveLinkRepository;

    @Override
    @Transactional
    public Mono<LinkResponseDto> createShortLink(LinkRequestDto request) {
        return uniqueAliasValidator.validate(request.alias())
                .flatMap(isUnique -> {
                    if (!isUnique) {
                        return Mono.error(new DuplicateAliasException(
                                "Alias '" + request.alias() + "' already exists"));
                    }
                    return keyGenerator.generateUniqueKey()
                            .flatMap(shortKey -> {
                                var entity = linkMapper.toLinkEntity(request, shortKey);
                                return reactiveLinkRepository.save(entity);
                            })
                            .map(linkMapper::toLinkResponseDto);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LinkRedirectDto> getRedirectData(String shortKey) {
        return reactiveLinkRepository.findByShortKey(shortKey)
                .switchIfEmpty(Mono.error(new LinkNotFoundException("Link not found")))
                .flatMap(link -> compositeLinkValidator.validate(link).thenReturn(link))
                .map(link -> new LinkRedirectDto(link.getOriginalUrl(), HttpStatus.MOVED_PERMANENTLY));
    }

    @Override
    @Transactional
    public Mono<Void> incrementClickCount(String shortKey) {
        return reactiveLinkRepository.incrementClickCountByShortKey(shortKey)
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated == null || rowsUpdated == 0) {
                        return Mono.error(new LinkNotFoundException("Link not found: " + shortKey));
                    }
                    return Mono.empty();
                })
                .then();
    }
}