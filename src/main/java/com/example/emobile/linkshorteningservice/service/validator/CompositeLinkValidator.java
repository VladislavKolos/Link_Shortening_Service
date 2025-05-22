package com.example.emobile.linkshorteningservice.service.validator;

import com.example.emobile.linkshorteningservice.model.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompositeLinkValidator {
    private final List<LinkValidator> linkValidators;

    public void validate(Link link) {
        linkValidators.forEach(validator -> validator.validate(link));
    }
}