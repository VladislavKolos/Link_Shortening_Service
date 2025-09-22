package com.example.emobile.linkshorteningservice.util;

import com.example.emobile.linkshorteningservice.util.constant.LinkUriPathConstant;
import lombok.experimental.UtilityClass;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@UtilityClass
public class UriBuilderUtil {

    public static URI createLinkUri(String shortKey) {
        return UriComponentsBuilder.fromPath(LinkUriPathConstant.LINK_BY_KEY_PATH)
                .buildAndExpand(shortKey)
                .toUri();
    }
}