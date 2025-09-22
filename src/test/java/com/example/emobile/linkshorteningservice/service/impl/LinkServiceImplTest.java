package com.example.emobile.linkshorteningservice.service.impl;

import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.exception.DuplicateAliasException;
import com.example.emobile.linkshorteningservice.exception.LinkNotFoundException;
import com.example.emobile.linkshorteningservice.mapper.LinkMapper;
import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import com.example.emobile.linkshorteningservice.repository.ReactiveLinkRepository;
import com.example.emobile.linkshorteningservice.service.generator.KeyGenerator;
import com.example.emobile.linkshorteningservice.service.validator.CompositeLinkValidator;
import com.example.emobile.linkshorteningservice.service.validator.UniqueAliasValidator;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceImplTest {

    @Mock
    private LinkMapper linkMapper;

    @Mock
    private KeyGenerator keyGenerator;

    @Mock
    private UniqueAliasValidator uniqueAliasValidator;

    @Mock
    private CompositeLinkValidator compositeLinkValidator;

    @Mock
    private ReactiveLinkRepository reactiveLinkRepository;

    @InjectMocks
    private LinkServiceImpl linkService;

    private LinkEntity linkEntity;
    private LinkRequestDto request;
    private LinkResponseDto response;

    @BeforeEach
    void setUp() {
        request = TestDataBuilderUtil.createValidLinkRequestDto();

        linkEntity = TestDataBuilderUtil.createValidLink();

        response = new LinkResponseDto(
                linkEntity.getShortKey(),
                linkEntity.getAlias(),
                linkEntity.getExpiresAt(),
                linkEntity.getIsActive());
    }

    @Test
    void createShortLink_whenAliasUnique_thenReturnsResponseSuccessfully() {
        String generatedKey = linkEntity.getShortKey();

        when(uniqueAliasValidator.validate(request.alias()))
                .thenReturn(Mono.just(true));
        when(keyGenerator.generateUniqueKey())
                .thenReturn(Mono.just(generatedKey));
        when(linkMapper.toLinkEntity(request, generatedKey))
                .thenReturn(linkEntity);
        when(reactiveLinkRepository.save(linkEntity))
                .thenReturn(Mono.just(linkEntity));
        when(linkMapper.toLinkResponseDto(linkEntity))
                .thenReturn(response);

        var result = linkService.createShortLink(request);

        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();

        verify(uniqueAliasValidator).validate(request.alias());
        verify(keyGenerator).generateUniqueKey();
        verify(linkMapper).toLinkEntity(request, generatedKey);
        verify(reactiveLinkRepository).save(linkEntity);
        verify(linkMapper).toLinkResponseDto(linkEntity);
        verifyNoMoreInteractions(uniqueAliasValidator, keyGenerator, linkMapper, reactiveLinkRepository);
        verifyNoInteractions(compositeLinkValidator);
    }

    @Test
    void createShortLink_whenAliasDuplicate_thenThrowsDuplicateAliasExceptionWithMessage() {
        when(uniqueAliasValidator.validate(request.alias()))
                .thenReturn(Mono.just(false));

        var result = linkService.createShortLink(request);

        String expectedMessage = "Alias '" + request.alias() + "' already exists";

        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(DuplicateAliasException.class);
                    assertThat(ex.getMessage()).isEqualTo(expectedMessage);
                })
                .verify();

        verify(uniqueAliasValidator).validate(request.alias());
        verifyNoMoreInteractions(uniqueAliasValidator);
        verifyNoInteractions(keyGenerator, linkMapper, reactiveLinkRepository, compositeLinkValidator);
    }

    @Test
    void getRedirectData_whenShortKeyExists_thenReturnsRedirectDtoSuccessfully() {
        when(reactiveLinkRepository.findByShortKey(linkEntity.getShortKey()))
                .thenReturn(Mono.just(linkEntity));
        when(compositeLinkValidator.validate(linkEntity))
                .thenReturn(Mono.empty());

        var result = linkService.getRedirectData(linkEntity.getShortKey());

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.originalUrl()).isEqualTo(linkEntity.getOriginalUrl());
                    assertThat(dto.status()).isNotNull();
                })
                .verifyComplete();

        verify(compositeLinkValidator).validate(linkEntity);
        verify(reactiveLinkRepository).findByShortKey(linkEntity.getShortKey());
        verifyNoMoreInteractions(reactiveLinkRepository, compositeLinkValidator);
        verifyNoInteractions(keyGenerator, linkMapper, uniqueAliasValidator);
    }

    @Test
    void getRedirectData_whenShortKeyMissing_thenThrowsLinkNotFoundExceptionWithMessage() {
        String missing = "notExist123";

        when(reactiveLinkRepository.findByShortKey(missing))
                .thenReturn(Mono.empty());

        var result = linkService.getRedirectData(missing);

        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(LinkNotFoundException.class);
                    assertThat(ex.getMessage()).isEqualTo("Link not found");
                })
                .verify();

        verify(reactiveLinkRepository).findByShortKey(missing);
        verifyNoMoreInteractions(reactiveLinkRepository);
        verifyNoInteractions(keyGenerator, linkMapper, uniqueAliasValidator, compositeLinkValidator);
    }

    @Test
    void incrementClickCount_whenShortKeyExists_thenCompletesSuccessfully() {
        int rowsUpdated = 1;

        when(reactiveLinkRepository.incrementClickCountByShortKey(linkEntity.getShortKey()))
                .thenReturn(Mono.just(rowsUpdated));

        var result = linkService.incrementClickCount(linkEntity.getShortKey());

        StepVerifier.create(result)
                .verifyComplete();

        verify(reactiveLinkRepository).incrementClickCountByShortKey(linkEntity.getShortKey());
        verifyNoMoreInteractions(reactiveLinkRepository);
        verifyNoInteractions(keyGenerator, linkMapper, uniqueAliasValidator, compositeLinkValidator);
    }

    @Test
    void incrementClickCount_whenShortKeyMissing_thenThrowsLinkNotFoundExceptionWithMessage() {
        int rowsUpdated = 0;

        String missing = "missing123";

        when(reactiveLinkRepository.incrementClickCountByShortKey(missing))
                .thenReturn(Mono.just(rowsUpdated));

        var result = linkService.incrementClickCount(missing);

        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(LinkNotFoundException.class);
                    assertThat(ex.getMessage()).contains(missing);
                })
                .verify();

        verify(reactiveLinkRepository).incrementClickCountByShortKey(missing);
        verifyNoMoreInteractions(reactiveLinkRepository);
        verifyNoInteractions(keyGenerator, linkMapper, uniqueAliasValidator, compositeLinkValidator);
    }
}