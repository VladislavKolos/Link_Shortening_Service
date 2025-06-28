package com.example.emobile.linkshorteningservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record LinkResponseDto(
        String shortKey,
        String alias,
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm") OffsetDateTime expiresAt,
        boolean isActive
) {
}