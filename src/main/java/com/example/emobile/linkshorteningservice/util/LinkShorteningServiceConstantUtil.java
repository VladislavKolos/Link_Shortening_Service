package com.example.emobile.linkshorteningservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LinkShorteningServiceConstantUtil {
    public static final String PRAGMA_NO_CACHE = "no-cache";
    public static final String CACHE_CONTROL_NO_CACHE = "no-cache, no-store, must-revalidate";
    public static final long EXPIRES_IMMEDIATELY = 0L;
    public static final String LOCATION_HEADER = "Location";
    public static final long CLICK_INCREMENT = 1L;
}