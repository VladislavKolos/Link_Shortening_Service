package com.example.emobile.linkshorteningservice.service.validator;

import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompositeLinkValidator {
    private final List<LinkValidator> linkValidators;

    public Mono<Void> validate(LinkEntity linkEntity) {
        return Flux.fromIterable(linkValidators)
                .flatMap(linkValidator -> linkValidator.validate(linkEntity))
                .then();
    }
}