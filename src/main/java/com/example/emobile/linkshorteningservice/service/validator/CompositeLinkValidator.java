package com.example.emobile.linkshorteningservice.service.validator;

import com.example.emobile.linkshorteningservice.model.LinkEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompositeLinkValidator {
    private final List<LinkValidator> linkValidators;

    public void validate(LinkEntity linkEntity) {
        linkValidators.forEach(validator -> validator.validate(linkEntity));
    }
}