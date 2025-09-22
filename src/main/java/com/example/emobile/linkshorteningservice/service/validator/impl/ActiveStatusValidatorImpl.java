package com.example.emobile.linkshorteningservice.service.validator.impl;

import com.example.emobile.linkshorteningservice.exception.LinkExpiredException;
import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import com.example.emobile.linkshorteningservice.service.validator.LinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ActiveStatusValidatorImpl implements LinkValidator {

    @Override
    public Mono<Void> validate(LinkEntity linkEntity) {
        if (Boolean.FALSE.equals(linkEntity.getIsActive())) {
            return Mono.error(new LinkExpiredException("Link is inactive"));
        }
        return Mono.empty();
    }
}