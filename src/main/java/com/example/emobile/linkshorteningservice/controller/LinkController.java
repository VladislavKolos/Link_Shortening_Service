package com.example.emobile.linkshorteningservice.controller;

import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.service.LinkService;
import com.example.emobile.linkshorteningservice.util.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.example.emobile.linkshorteningservice.util.constant.HttpHeaderConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/links")
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    public Mono<ResponseEntity<LinkResponseDto>> createShortLink(@Valid @RequestBody LinkRequestDto request) {
        return linkService.createShortLink(request)
                .map(response -> {
                    var location = UriBuilderUtil.createLinkUri(response.shortKey());

                    return ResponseEntity.created(location).body(response);
                });
    }

    @GetMapping("/{shortKey}")
    public Mono<Void> redirectToOriginalUrl(@PathVariable String shortKey, ServerHttpResponse response) {
        return linkService.getRedirectData(shortKey)
                .flatMap(linkRedirectDto ->
                        linkService.incrementClickCount(shortKey)
                                .then(Mono.defer(() -> {
                                    response.getHeaders().setCacheControl(CACHE_CONTROL_NO_CACHE);
                                    response.getHeaders().add(HttpHeaders.PRAGMA, PRAGMA_NO_CACHE);
                                    response.getHeaders().setExpires(EXPIRES_IMMEDIATELY);
                                    response.getHeaders().setLocation(URI.create(linkRedirectDto.originalUrl()));
                                    response.setStatusCode(linkRedirectDto.status());

                                    return response.setComplete();
                                }))
                );
    }
}