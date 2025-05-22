package com.example.emobile.linkshorteningservice.service.validator.impl;

import com.example.emobile.linkshorteningservice.exception.LinkExpiredException;
import com.example.emobile.linkshorteningservice.model.Link;
import com.example.emobile.linkshorteningservice.service.validator.LinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveStatusValidatorImpl implements LinkValidator {

    @Override
    public void validate(Link link) {
        if (!link.getIsActive()) {
            throw new LinkExpiredException("Link is inactive");
        }
    }
}