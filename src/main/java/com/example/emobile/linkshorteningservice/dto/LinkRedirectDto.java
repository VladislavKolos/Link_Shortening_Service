package com.example.emobile.linkshorteningservice.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record LinkRedirectDto(
        String originalUrl,
        HttpStatus status
) {
}