package com.example.emobile.linkshorteningservice.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ExceptionDto(
        int errorCode,
        String message,
        String path,
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime timestamp
) {
}