package com.example.emobile.linkshorteningservice.exception;

public abstract class LinkShorteningServiceException extends RuntimeException {
    public LinkShorteningServiceException(String message) {
        super(message);
    }
}