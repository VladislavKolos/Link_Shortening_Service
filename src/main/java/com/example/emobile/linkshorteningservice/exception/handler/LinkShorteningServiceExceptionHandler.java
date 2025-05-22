package com.example.emobile.linkshorteningservice.exception.handler;

import com.example.emobile.linkshorteningservice.exception.InvalidTtlException;
import com.example.emobile.linkshorteningservice.exception.KeyGenerationException;
import com.example.emobile.linkshorteningservice.exception.LinkExpiredException;
import com.example.emobile.linkshorteningservice.exception.LinkNotFoundException;
import com.example.emobile.linkshorteningservice.exception.builder.ValidationExceptionMessageBuilder;
import com.example.emobile.linkshorteningservice.exception.dto.ExceptionDto;
import com.example.emobile.linkshorteningservice.exception.enums.ErrorMessage;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class LinkShorteningServiceExceptionHandler {
    private final ValidationExceptionMessageBuilder validationExceptionMessageBuilder;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionDto handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        var result = ex.getBindingResult();

        List<FieldError> fieldErrors = result.getFieldErrors();

        if (!fieldErrors.isEmpty()) {
            String errorMessage = validationExceptionMessageBuilder.buildValidationErrorMessage(fieldErrors);

            return buildExceptionResponse(ErrorMessage.METHOD_ARGUMENT_NOT_VALID_ERROR, errorMessage,
                    getRequestPath(request));
        }
        return buildExceptionResponse(ErrorMessage.METHOD_ARGUMENT_NOT_VALID_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTtlException.class)
    public ExceptionDto handleInvalidTtlException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.INVALID_TTL_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(KeyGenerationException.class)
    public ExceptionDto handleKeyGenerationException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.KEY_GENERATION_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(LinkExpiredException.class)
    public ExceptionDto handleLinkExpiredException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.LINK_EXPIRED_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LinkNotFoundException.class)
    public ExceptionDto handleLinkNotFoundException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.LINK_NOT_FOUND_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ExceptionDto handleOptimisticLockException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.OPTIMISTIC_LOCK_ERROR, getRequestPath(request));
    }

    private String getRequestPath(WebRequest request) {
        return request.getDescription(false)
                .replace("uri=", "");
    }

    private ExceptionDto buildExceptionResponse(ErrorMessage errorMessage, String path) {
        return buildExceptionResponse(errorMessage, errorMessage.getMessage(), path);
    }

    private ExceptionDto buildExceptionResponse(ErrorMessage errorMessage, String customMessage, String path) {
        return ExceptionDto.builder()
                .errorCode(errorMessage.getErrorCode())
                .message(customMessage)
                .path(path)
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
    }
}