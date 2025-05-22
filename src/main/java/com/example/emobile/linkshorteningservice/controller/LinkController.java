package com.example.emobile.linkshorteningservice.controller;

import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;
import com.example.emobile.linkshorteningservice.service.LinkService;
import com.example.emobile.linkshorteningservice.service.UriService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.emobile.linkshorteningservice.util.LinkShorteningServiceConstantUtil.*;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.EXPIRES;
import static org.springframework.http.HttpHeaders.PRAGMA;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/links")
public class LinkController {
    private final UriService uriService;
    private final LinkService linkService;

    @PostMapping
    public ResponseEntity<LinkResponseDto> createShortLink(@Valid @RequestBody LinkRequestDto request) {
        var response = linkService.createShortLink(request);
        var location = uriService.createLinkUri(response.shortKey());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{shortKey}")
    public void redirectToOriginalUrl(@PathVariable String shortKey, HttpServletResponse response) {
        var redirectData = linkService.getRedirectData(shortKey);

        response.setHeader(CACHE_CONTROL, CACHE_CONTROL_NO_CACHE);
        response.setHeader(PRAGMA, PRAGMA_NO_CACHE);
        response.setDateHeader(EXPIRES, EXPIRES_IMMEDIATELY);
        response.setHeader(LOCATION_HEADER, redirectData.originalUrl());
        response.setStatus(redirectData.status().value());

        linkService.incrementClickCount(shortKey);
    }
}