package com.example.emobile.linkshorteningservice.service.validator;

import com.example.emobile.linkshorteningservice.model.Link;

@FunctionalInterface
public interface LinkValidator {
    void validate(Link link);
}