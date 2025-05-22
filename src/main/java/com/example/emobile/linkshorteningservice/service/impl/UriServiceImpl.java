package com.example.emobile.linkshorteningservice.service.impl;

import com.example.emobile.linkshorteningservice.service.LinkUriConstant;
import com.example.emobile.linkshorteningservice.service.UriService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class UriServiceImpl implements UriService {

    @Override
    public URI createLinkUri(String shortKey) {
        return UriComponentsBuilder.fromPath(LinkUriConstant.LINK_BY_KEY_PATH)
                .buildAndExpand(shortKey)
                .toUri();
    }
}