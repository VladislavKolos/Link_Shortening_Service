package com.example.emobile.linkshorteningservice.service.validator;

import com.example.emobile.linkshorteningservice.model.LinkEntity;

@FunctionalInterface
public interface LinkValidator {
    void validate(LinkEntity linkEntity);
}