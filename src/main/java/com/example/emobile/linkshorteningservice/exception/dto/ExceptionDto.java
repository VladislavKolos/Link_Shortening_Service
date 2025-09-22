package com.example.emobile.linkshorteningservice.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record ExceptionDto(
        int errorCode,
        String message,
        String path,
        @JsonFormat(pattern = "dd.MM.yyyy HH:mmXXX") OffsetDateTime timestamp
) {
}