package com.example.emobile.linkshorteningservice.service.generator;

import com.example.emobile.linkshorteningservice.exception.KeyGenerationException;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

@Service
public class KeyGenerator {
    private static final int KEY_LENGTH = 8;
    private static final int MAX_ATTEMPTS = 100;
    private static final int KEY_START_INDEX = 0;

    public String generateUniqueKey(KeyExistenceChecker existenceChecker) {
        return Stream.generate(this::generateKey)
                .limit(MAX_ATTEMPTS)
                .filter(key -> !existenceChecker.exists(key))
                .findFirst()
                .orElseThrow(() -> new KeyGenerationException(
                        "Failed to generate unique key after " + MAX_ATTEMPTS + " attempts"));
    }

    private String generateKey() {
        return UUID.randomUUID().toString().substring(KEY_START_INDEX, KEY_LENGTH);
    }

    @FunctionalInterface
    public interface KeyExistenceChecker {
        boolean exists(String key);
    }
}