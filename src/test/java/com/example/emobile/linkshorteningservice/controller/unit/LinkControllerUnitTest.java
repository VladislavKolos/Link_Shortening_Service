package com.example.emobile.linkshorteningservice.controller.unit;

import com.example.emobile.linkshorteningservice.config.LinkControllerUnitTestConfig;
import com.example.emobile.linkshorteningservice.controller.LinkController;
import com.example.emobile.linkshorteningservice.dto.LinkRedirectDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.exception.DuplicateAliasException;
import com.example.emobile.linkshorteningservice.exception.LinkNotFoundException;
import com.example.emobile.linkshorteningservice.exception.enums.ErrorMessage;
import com.example.emobile.linkshorteningservice.service.LinkService;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.example.emobile.linkshorteningservice.util.constant.HttpHeaderConstant.CACHE_CONTROL_NO_CACHE;
import static com.example.emobile.linkshorteningservice.util.constant.HttpHeaderConstant.PRAGMA_NO_CACHE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("test")
@Import(LinkControllerUnitTestConfig.class)
@WebFluxTest(controllers = LinkController.class)
class LinkControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LinkService linkService;

    @BeforeEach
    void resetMocks() {
        reset(linkService);
    }

    @Test
    void createShortLink_whenValid_thenReturnsCreated() {
        var request = TestDataBuilderUtil.createValidLinkRequestDto();
        var linkEntity = TestDataBuilderUtil.createValidLink();
        var response = new LinkResponseDto(
                linkEntity.getShortKey(),
                linkEntity.getAlias(),
                linkEntity.getExpiresAt(),
                linkEntity.getIsActive());

        when(linkService.createShortLink(request))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v2/links")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody(LinkResponseDto.class)
                .value(responseDto -> {
                    assertThat(responseDto.shortKey()).isEqualTo(responseDto.shortKey());
                    assertThat(responseDto.alias()).isEqualTo(response.alias());
                    assertThat(responseDto.isActive()).isTrue();
                });

        verify(linkService).createShortLink(request);
        verifyNoMoreInteractions(linkService);
    }

    @Test
    void createShortLink_whenInvalid_thenReturnsBadRequest() {
        var invalidRequest = TestDataBuilderUtil.createInvalidLinkRequestDto();

        webTestClient.post()
                .uri("/api/v2/links")
                .contentType(APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(400)
                .jsonPath("$.message").value(messageValue -> {
                    String errorMessageString = String.valueOf(messageValue);
                    assertThat(errorMessageString).contains("Validation failed for fields");
                    assertThat(errorMessageString).contains("originalUrl");
                    assertThat(errorMessageString).contains("ttlInSeconds");
                })
                .jsonPath("$.path").isEqualTo("/api/v2/links")
                .jsonPath("$.timestamp").exists();

        verifyNoInteractions(linkService);
    }

    @Test
    void createShortLink_whenAliasDuplicate_thenReturns409() {
        var request = TestDataBuilderUtil.createValidLinkRequestDto();

        when(linkService.createShortLink(request))
                .thenReturn(Mono.error(new DuplicateAliasException(ErrorMessage.DUPLICATE_ALIAS_ERROR.getMessage())));

        webTestClient.post()
                .uri("/api/v2/links")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(409)
                .jsonPath("$.message").isEqualTo(ErrorMessage.DUPLICATE_ALIAS_ERROR.getMessage())
                .jsonPath("$.path").isEqualTo("/api/v2/links")
                .jsonPath("$.timestamp").exists();

        verify(linkService).createShortLink(request);
        verifyNoMoreInteractions(linkService);
    }

    @Test
    void redirectToOriginalUrl_whenKeyExists_thenReturns301() {
        String shortKey = "abc12345";
        String originalUrl = "https://example.com";
        var redirectDto = new LinkRedirectDto(originalUrl, HttpStatus.MOVED_PERMANENTLY);

        when(linkService.getRedirectData(shortKey))
                .thenReturn(Mono.just(redirectDto));
        when(linkService.incrementClickCount(shortKey))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v2/links/{shortKey}", shortKey)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.MOVED_PERMANENTLY)
                .expectHeader().valueEquals("Location", originalUrl)
                .expectHeader().valueEquals("Cache-Control", CACHE_CONTROL_NO_CACHE)
                .expectHeader().valueEquals("Pragma", PRAGMA_NO_CACHE)
                .expectHeader().exists("Expires")
                .expectBody().isEmpty();

        verify(linkService).getRedirectData(shortKey);
        verify(linkService).incrementClickCount(shortKey);
        verifyNoMoreInteractions(linkService);
    }

    @Test
    void redirectToOriginalUrl_whenKeyMissing_thenReturns404() {
        String missingKey = "missing123";

        when(linkService.getRedirectData(missingKey))
                .thenReturn(Mono.error(new LinkNotFoundException(
                        ErrorMessage.LINK_NOT_FOUND_ERROR.getMessage())));

        webTestClient.get()
                .uri("/api/v2/links/{shortKey}", missingKey)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo(ErrorMessage.LINK_NOT_FOUND_ERROR.getMessage())
                .jsonPath("$.path").isEqualTo("/api/v2/links/" + missingKey)
                .jsonPath("$.timestamp").exists();

        verify(linkService).getRedirectData(missingKey);
        verifyNoMoreInteractions(linkService);
    }
}