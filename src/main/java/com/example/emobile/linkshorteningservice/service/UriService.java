package com.example.emobile.linkshorteningservice.service;

import java.net.URI;

public interface UriService {
    URI createLinkUri(String shortKey);
}