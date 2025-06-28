package com.example.emobile.linkshorteningservice.service.validator.impl;

import com.example.emobile.linkshorteningservice.exception.LinkExpiredException;
import com.example.emobile.linkshorteningservice.model.LinkEntity;
import com.example.emobile.linkshorteningservice.service.validator.LinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ExpirationValidatorImpl implements LinkValidator {

    @Override
    public void validate(LinkEntity linkEntity) {
        if (linkEntity.getExpiresAt() != null && linkEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new LinkExpiredException("Link has expired");
        }
    }
}