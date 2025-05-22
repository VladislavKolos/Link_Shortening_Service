package com.example.emobile.linkshorteningservice.service.impl;

import com.example.emobile.linkshorteningservice.dto.LinkRedirectDto;
import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.exception.LinkNotFoundException;
import com.example.emobile.linkshorteningservice.mapper.LinkMapper;
import com.example.emobile.linkshorteningservice.repository.LinkRepository;
import com.example.emobile.linkshorteningservice.service.LinkService;
import com.example.emobile.linkshorteningservice.service.async.AsyncClickService;
import com.example.emobile.linkshorteningservice.service.generator.KeyGenerator;
import com.example.emobile.linkshorteningservice.service.validator.CompositeLinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkMapper linkMapper;
    private final KeyGenerator keyGenerator;
    private final LinkRepository linkRepository;
    private final AsyncClickService asyncClickService;
    private final CompositeLinkValidator compositeLinkValidator;

    @Override
    @Transactional
    public LinkResponseDto createShortLink(LinkRequestDto request) {
        String shortKey = keyGenerator.generateUniqueKey(linkRepository::existsByShortKey);

        var entity = linkMapper.toLinkEntity(request, shortKey);
        var savedEntity = linkRepository.save(entity);

        return linkMapper.toLinkResponseDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkRedirectDto getRedirectData(String shortKey) {
        var link = linkRepository.findByShortKey(shortKey)
                .orElseThrow(() -> new LinkNotFoundException("Link not found"));

        compositeLinkValidator.validate(link);

        return LinkRedirectDto.builder()
                .originalUrl(link.getOriginalUrl())
                .status(HttpStatus.MOVED_PERMANENTLY)
                .build();
    }

    @Override
    @Async("taskExecutor")
    public void incrementClickCount(String shortKey) {
        asyncClickService.incrementInTransaction(shortKey);
    }
}