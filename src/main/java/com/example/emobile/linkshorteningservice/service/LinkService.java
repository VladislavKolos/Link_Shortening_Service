package com.example.emobile.linkshorteningservice.service;

import com.example.emobile.linkshorteningservice.dto.LinkRedirectDto;
import com.example.emobile.linkshorteningservice.dto.request.LinkRequestDto;
import com.example.emobile.linkshorteningservice.dto.response.LinkResponseDto;

public interface LinkService {
    LinkResponseDto createShortLink(LinkRequestDto request);

    LinkRedirectDto getRedirectData(String shortKey);

    void incrementClickCount(String shortKey);
}