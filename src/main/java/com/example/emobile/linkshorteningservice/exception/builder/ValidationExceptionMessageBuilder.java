package com.example.emobile.linkshorteningservice.exception.builder;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationExceptionMessageBuilder {
    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed for fields: ";

    public String buildValidationErrorMessage(List<FieldError> fieldErrors) {
        return VALIDATION_FAILED_MESSAGE + fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + " (" + fieldError.getDefaultMessage() + ")")
                .collect(Collectors.joining(", "));
    }
}