package com.example.emobile.linkshorteningservice.exception.handler;

import com.example.emobile.linkshorteningservice.exception.*;
import com.example.emobile.linkshorteningservice.exception.builder.ValidationExceptionMessageBuilder;
import com.example.emobile.linkshorteningservice.exception.dto.ExceptionDto;
import com.example.emobile.linkshorteningservice.exception.enums.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class LinkShorteningServiceExceptionHandler {
    private final ValidationExceptionMessageBuilder validationExceptionMessageBuilder;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ExceptionDto> handleWebExchangeBindErrors(WebExchangeBindException ex, ServerWebExchange exchange) {
        List<FieldError> fieldErrors = ex.getFieldErrors();

        if (!fieldErrors.isEmpty()) {
            String errorMessage = validationExceptionMessageBuilder.buildValidationErrorMessage(fieldErrors);

            return Mono.just(buildExceptionResponse(ErrorMessage.METHOD_ARGUMENT_NOT_VALID_ERROR, errorMessage,
                    exchange));
        }
        return Mono.just(buildExceptionResponse(ErrorMessage.METHOD_ARGUMENT_NOT_VALID_ERROR, exchange));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTtlException.class)
    public Mono<ExceptionDto> handleInvalidTtlException(ServerWebExchange exchange) {
        return Mono.just(buildExceptionResponse(ErrorMessage.INVALID_TTL_ERROR, exchange));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(KeyGenerationException.class)
    public Mono<ExceptionDto> handleKeyGenerationException(ServerWebExchange exchange) {
        return Mono.just(buildExceptionResponse(ErrorMessage.KEY_GENERATION_ERROR, exchange));
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(LinkExpiredException.class)
    public Mono<ExceptionDto> handleLinkExpiredException(ServerWebExchange exchange) {
        return Mono.just(buildExceptionResponse(ErrorMessage.LINK_EXPIRED_ERROR, exchange));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LinkNotFoundException.class)
    public Mono<ExceptionDto> handleLinkNotFoundException(ServerWebExchange exchange) {
        return Mono.just(buildExceptionResponse(ErrorMessage.LINK_NOT_FOUND_ERROR, exchange));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateAliasException.class)
    public Mono<ExceptionDto> handleDuplicateAliasException(ServerWebExchange exchange) {
        return Mono.just(buildExceptionResponse(ErrorMessage.DUPLICATE_ALIAS_ERROR, exchange));
    }

    private ExceptionDto buildExceptionResponse(ErrorMessage errorMessage, ServerWebExchange exchange) {
        return buildExceptionResponse(errorMessage, errorMessage.getMessage(), exchange);
    }

    private ExceptionDto buildExceptionResponse(ErrorMessage errorMessage, String customMessage,
                                                ServerWebExchange exchange) {
        return new ExceptionDto(
                errorMessage.getErrorCode(),
                customMessage,
                exchange.getRequest().getPath().value(),
                OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}