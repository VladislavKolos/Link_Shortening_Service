package com.example.emobile.linkshorteningservice.exception;

public class LinkExpiredException extends LinkShorteningServiceException {
    public LinkExpiredException(String message) {
        super(message);
    }
}