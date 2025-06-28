package com.example.emobile.linkshorteningservice.service.impl;

import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.exception.LinkNotFoundException;
import com.example.emobile.linkshorteningservice.mapper.LinkMapper;
import com.example.emobile.linkshorteningservice.model.LinkEntity;
import com.example.emobile.linkshorteningservice.repository.LinkRepository;
import com.example.emobile.linkshorteningservice.service.async.AsyncClickService;
import com.example.emobile.linkshorteningservice.service.generator.KeyGenerator;
import com.example.emobile.linkshorteningservice.service.validator.CompositeLinkValidator;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceImplTest {

    @Mock
    private LinkMapper linkMapper;

    @Mock
    private KeyGenerator keyGenerator;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private AsyncClickService asyncClickService;

    @Mock
    private CompositeLinkValidator compositeLinkValidator;

    @InjectMocks
    private LinkServiceImpl linkService;

    private LinkEntity linkEntity;
    private LinkRequestDto request;
    private LinkResponseDto response;

    @BeforeEach
    public void setUp() {
        request = TestDataBuilderUtil.createValidLinkRequestDto();
        linkEntity = TestDataBuilderUtil.createValidLink();

        response = new LinkResponseDto(
                linkEntity.getShortKey(),
                linkEntity.getAlias(),
                linkEntity.getExpiresAt(),
                linkEntity.getIsActive());
    }

    @Test
    public void shouldCreateShortLinkSuccessfully() {
        String generatedKey = linkEntity.getShortKey();

        when(keyGenerator.generateUniqueKey(any())).thenReturn(generatedKey);

        when(linkMapper.toLinkEntity(request, generatedKey)).thenReturn(linkEntity);
        when(linkRepository.save(linkEntity)).thenReturn(linkEntity);
        when(linkMapper.toLinkResponseDto(linkEntity)).thenReturn(response);

        var actualResponse = linkService.createShortLink(request);

        assertThat(actualResponse).isEqualTo(response);

        verify(linkMapper).toLinkEntity(request, generatedKey);
        verify(linkRepository).save(linkEntity);
        verify(linkMapper).toLinkResponseDto(linkEntity);
    }

    @Test
    public void shouldReturnRedirectDataWhenLinkIsActiveAndNotExpired() {
        when(linkRepository.findByShortKey(linkEntity.getShortKey())).thenReturn(Optional.of(linkEntity));

        var redirectDto = linkService.getRedirectData(linkEntity.getShortKey());

        assertThat(redirectDto.originalUrl()).isEqualTo(linkEntity.getOriginalUrl());
        assertThat(redirectDto.status()).isEqualTo(HttpStatus.MOVED_PERMANENTLY);

        verify(compositeLinkValidator).validate(linkEntity);
    }

    @Test
    public void shouldThrowExceptionWhenLinkNotFound() {
        String missingKey = "notExist123";

        when(linkRepository.findByShortKey(missingKey)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> linkService.getRedirectData(missingKey))
                .isInstanceOf(LinkNotFoundException.class)
                .hasMessage("Link not found");

        verifyNoInteractions(compositeLinkValidator);
    }

    @Test
    public void shouldCallAsyncClickServiceWhenIncrementClickCount() {
        linkService.incrementClickCount(linkEntity.getShortKey());

        verify(asyncClickService).incrementInTransaction(linkEntity.getShortKey());
    }

    @Test
    public void shouldDeactivateLinkWhenExpiredOnPersistOrUpdate() {
        var expiredLink = TestDataBuilderUtil.createExpiredLink();
        expiredLink.checkExpiration();

        assertThat(expiredLink.getIsActive()).isFalse();
    }
}