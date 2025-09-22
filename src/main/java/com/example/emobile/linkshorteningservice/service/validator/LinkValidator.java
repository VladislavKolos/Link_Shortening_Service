package com.example.emobile.linkshorteningservice.service.validator;

import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface LinkValidator {
    Mono<Void> validate(LinkEntity linkEntity);
}