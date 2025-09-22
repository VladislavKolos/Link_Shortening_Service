package com.example.emobile.linkshorteningservice.mapper;

import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.exception.InvalidTtlException;
import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface LinkMapper {
    long MIN_TTL_VALUE = 1L;
    long INITIAL_CLICK_COUNT = 0L;
    boolean DEFAULT_ACTIVE_STATUS = true;

    @Mapping(target = "isActive", source = "entity", qualifiedByName = "isActive")
    LinkResponseDto toLinkResponseDto(LinkEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "clickCount", source = "request", qualifiedByName = "setInitialClickCount")
    @Mapping(target = "isActive", source = "request", qualifiedByName = "setInitialActiveStatus")
    @Mapping(target = "expiresAt", source = "request.ttlInSeconds", qualifiedByName = "calculateExpiresAt")
    LinkEntity toLinkEntity(LinkRequestDto request, String shortKey);

    @Named("isActive")
    default boolean isActive(LinkEntity entity) {
        return entity.getIsActive() &&
                (entity.getExpiresAt() == null || entity.getExpiresAt().isAfter(OffsetDateTime.now()));
    }

    @Named("calculateExpiresAt")
    default OffsetDateTime calculateExpiresAt(Long ttlInSeconds) {
        if (ttlInSeconds != null && ttlInSeconds < MIN_TTL_VALUE) {
            throw new InvalidTtlException("TTL must be at least " + MIN_TTL_VALUE + " second");
        }
        return ttlInSeconds != null ? OffsetDateTime.now().plusSeconds(ttlInSeconds) : null;
    }

    @Named("setInitialClickCount")
    default long setInitialClickCount(LinkRequestDto request) {
        return INITIAL_CLICK_COUNT;
    }

    @Named("setInitialActiveStatus")
    default boolean setInitialActiveStatus(LinkRequestDto request) {
        return DEFAULT_ACTIVE_STATUS;
    }
}