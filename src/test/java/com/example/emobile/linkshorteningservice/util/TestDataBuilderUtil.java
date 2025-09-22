package com.example.emobile.linkshorteningservice.util;

import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.util.UUID;

@UtilityClass
public final class TestDataBuilderUtil {
    public static LinkEntity createValidLink() {
        return defaultLinkBuilder().build();
    }

    public static LinkEntity.LinkEntityBuilder<?, ?> defaultLinkBuilder() {
        return LinkEntity.builder()
                .originalUrl("https://example.com")
                .shortKey(UUID.randomUUID().toString().substring(0, 8))
                .alias(null)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .isActive(true)
                .clickCount(0L);
    }

    public static LinkRequestDto createValidLinkRequestDto() {
        return new LinkRequestDto("https://example.com/some/path", "customAlias123", 3600L);
    }

    public static LinkRequestDto createInvalidLinkRequestDto() {
        return new LinkRequestDto("not_a_valid_url", "!!@@##", -1L);
    }
}