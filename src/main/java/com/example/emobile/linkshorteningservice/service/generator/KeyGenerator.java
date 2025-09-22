package com.example.emobile.linkshorteningservice.service.generator;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface KeyGenerator {
    Mono<String> generateUniqueKey();
}