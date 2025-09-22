package com.example.emobile.linkshorteningservice.util.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LinkUriPathConstant {
    public static final String BASE_PATH = "/api/links";
    public static final String LINK_BY_KEY_PATH = BASE_PATH + "/{shortKey}";

}