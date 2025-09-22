package com.example.emobile.linkshorteningservice.config;

import com.example.emobile.linkshorteningservice.exception.builder.ValidationExceptionMessageBuilder;
import com.example.emobile.linkshorteningservice.exception.handler.LinkShorteningServiceExceptionHandler;
import com.example.emobile.linkshorteningservice.service.LinkService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class LinkControllerUnitTestConfig {

    @Bean
    public LinkService linkService() {
        return mock(LinkService.class);
    }

    @Bean
    public ValidationExceptionMessageBuilder validationExceptionMessageBuilder() {
        return new ValidationExceptionMessageBuilder();
    }

    @Bean
    public LinkShorteningServiceExceptionHandler linkShorteningServiceExceptionHandler(
            ValidationExceptionMessageBuilder builder) {
        return new LinkShorteningServiceExceptionHandler(builder);
    }
}