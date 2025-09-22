package com.example.emobile.linkshorteningservice.service.validator;

import com.example.emobile.linkshorteningservice.repository.ReactiveLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UniqueAliasValidator {
    private final ReactiveLinkRepository reactiveLinkRepository;

    @Transactional(readOnly = true)
    public Mono<Boolean> validate(String alias) {
        return reactiveLinkRepository.existsByAlias(alias)
                .map(exists -> !exists);
    }
}