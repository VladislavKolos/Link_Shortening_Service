package com.example.emobile.linkshorteningservice.controller.integration;

import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static com.example.emobile.linkshorteningservice.util.constant.HttpHeaderConstant.CACHE_CONTROL_NO_CACHE;
import static com.example.emobile.linkshorteningservice.util.constant.HttpHeaderConstant.PRAGMA_NO_CACHE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LinkControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @AfterEach
    void cleanupDatabase() {
        databaseClient.sql("DELETE FROM link")
                .fetch()
                .rowsUpdated()
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void createShortLink_whenValid_thenReturnsCreated() {
        var request = TestDataBuilderUtil.createValidLinkRequestDto();

        webTestClient.post()
                .uri("/api/v2/links")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody(LinkResponseDto.class)
                .value(response -> {
                    assertThat(response.shortKey()).isNotBlank();
                    assertThat(response.alias()).isEqualTo(request.alias());
                    assertThat(response.expiresAt()).isNotNull();
                    assertThat(response.isActive()).isTrue();
                });
    }

    @Test
    void createShortLink_whenInvalid_thenReturnsBadRequest() {
        var request = TestDataBuilderUtil.createInvalidLinkRequestDto();

        webTestClient.post()
                .uri("/api/v2/links")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
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
    }

    @Test
    void redirectToOriginalUrl_whenKeyExists_thenReturns301() {
        var request = TestDataBuilderUtil.createValidLinkRequestDto();

        var createdResponse = webTestClient.post()
                .uri("/api/v2/links")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkResponseDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(createdResponse);

        webTestClient.get()
                .uri("/api/v2/links/{shortKey}", createdResponse.shortKey())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", request.originalUrl())
                .expectHeader().valueEquals(CACHE_CONTROL_NO_CACHE)
                .expectHeader().valueEquals("Pragma", PRAGMA_NO_CACHE)
                .expectHeader().exists("Expires");
    }
}