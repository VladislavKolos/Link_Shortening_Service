package com.example.emobile.linkshorteningservice.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LinkUriConstant {
    public static final String BASE_PATH = "/api/links";
    public static final String LINK_BY_KEY_PATH = BASE_PATH + "/{shortKey}";
}