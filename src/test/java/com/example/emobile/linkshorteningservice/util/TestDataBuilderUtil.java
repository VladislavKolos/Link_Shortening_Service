package com.example.emobile.linkshorteningservice.util;

import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.model.Link;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public final class TestDataBuilderUtil {
    public static Link createValidLink() {
        return defaultLinkBuilder().build();
    }

    public static Link createExpiredLink() {
        return defaultLinkBuilder()
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    public static Link.LinkBuilder<?, ?> defaultLinkBuilder() {
        return Link.builder()
                .originalUrl("https://example.com")
                .shortKey(UUID.randomUUID().toString().substring(0, 8))
                .alias(null)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isActive(true)
                .clickCount(0L);
    }

    public static LinkRequestDto createValidLinkRequestDto() {
        return LinkRequestDto.builder()
                .originalUrl("https://example.com/some/path")
                .alias("customAlias123")
                .ttlInSeconds(3600L)
                .build();
    }

    public static LinkRequestDto createInvalidLinkRequestDto() {
        return LinkRequestDto.builder()
                .originalUrl("not_a_valid_url")
                .alias("!!@@##")
                .ttlInSeconds(-1L)
                .build();
    }
}