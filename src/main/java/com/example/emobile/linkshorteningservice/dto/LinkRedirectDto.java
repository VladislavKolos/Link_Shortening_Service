package com.example.emobile.linkshorteningservice.dto;

import org.springframework.http.HttpStatus;

public record LinkRedirectDto(
        String originalUrl,
        HttpStatus status
) {
}