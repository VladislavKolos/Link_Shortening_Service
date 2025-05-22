package com.example.emobile.linkshorteningservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LinkResponseDto(
        String shortKey,
        String alias,
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime expiresAt,
        boolean isActive
) {
}