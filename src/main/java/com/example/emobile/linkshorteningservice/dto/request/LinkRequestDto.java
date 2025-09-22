package com.example.emobile.linkshorteningservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record LinkRequestDto(

        @NotBlank
        @Size(max = 2048)
        @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].\\S*$")
        String originalUrl,

        @Size(min = 3, max = 255)
        @Pattern(regexp = "^[a-zA-Z0-9_-]*$")
        String alias,

        @Positive
        Long ttlInSeconds
) {
}