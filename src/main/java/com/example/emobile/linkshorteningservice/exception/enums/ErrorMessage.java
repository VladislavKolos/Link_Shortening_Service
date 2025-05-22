package com.example.emobile.linkshorteningservice.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    METHOD_ARGUMENT_NOT_VALID_ERROR(
            "Some fields are incorrect or missing. Please review your input and try again.",
            400),

    INVALID_TTL_ERROR(
            "The provided link expiration time is invalid. Please use a valid duration.",
            400),

    KEY_GENERATION_ERROR(
            "Unable to generate a short link at the moment. Please try again later.",
            500),

    LINK_EXPIRED_ERROR(
            "This link has expired and is no longer available.",
            410),

    LINK_NOT_FOUND_ERROR(
            "The requested link was not found. It may have been deleted or is incorrect.",
            404),

    OPTIMISTIC_LOCK_ERROR(
            "The data you tried to update has been modified by another process. Please try again.",
            409);

    private final String message;
    private final int errorCode;
}