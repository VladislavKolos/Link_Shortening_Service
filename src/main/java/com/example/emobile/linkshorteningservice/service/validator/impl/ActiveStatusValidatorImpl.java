package com.example.emobile.linkshorteningservice.service.validator.impl;

import com.example.emobile.linkshorteningservice.exception.LinkExpiredException;
import com.example.emobile.linkshorteningservice.model.LinkEntity;
import com.example.emobile.linkshorteningservice.service.validator.LinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveStatusValidatorImpl implements LinkValidator {

    @Override
    public void validate(LinkEntity linkEntity) {
        if (!linkEntity.getIsActive()) {
            throw new LinkExpiredException("Link is inactive");
        }
    }
}