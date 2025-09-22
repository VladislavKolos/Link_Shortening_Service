package com.example.emobile.linkshorteningservice.service.generator.impl;

import com.example.emobile.linkshorteningservice.repository.ReactiveLinkRepository;
import com.example.emobile.linkshorteningservice.service.generator.KeyGenerator;
import com.example.emobile.linkshorteningservice.util.KeyGenerationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveKeyGeneratorImpl implements KeyGenerator {
    private final ReactiveLinkRepository reactiveLinkRepository;

    @Override
    public Mono<String> generateUniqueKey() {
        return KeyGenerationUtil.generateUniqueKey(reactiveLinkRepository::existsByShortKey);
    }
}