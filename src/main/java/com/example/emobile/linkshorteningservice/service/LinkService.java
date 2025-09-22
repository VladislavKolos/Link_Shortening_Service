package com.example.emobile.linkshorteningservice.service;

import com.example.emobile.linkshorteningservice.dto.LinkRedirectDto;
import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import reactor.core.publisher.Mono;

public interface LinkService {
    Mono<LinkResponseDto> createShortLink(LinkRequestDto request);

    Mono<LinkRedirectDto> getRedirectData(String shortKey);

    Mono<Void> incrementClickCount(String shortKey);
}