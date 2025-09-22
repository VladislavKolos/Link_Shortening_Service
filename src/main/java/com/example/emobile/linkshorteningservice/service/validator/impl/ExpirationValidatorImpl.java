package com.example.emobile.linkshorteningservice.service.validator.impl;

import com.example.emobile.linkshorteningservice.exception.LinkExpiredException;
import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import com.example.emobile.linkshorteningservice.service.validator.LinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ExpirationValidatorImpl implements LinkValidator {

    @Override
    public Mono<Void> validate(LinkEntity linkEntity) {
        if (linkEntity.getExpiresAt() != null && linkEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            return Mono.error(new LinkExpiredException("Link has expired"));
        }
        return  Mono.empty();
    }
}