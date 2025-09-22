package com.example.emobile.linkshorteningservice.util;

import com.example.emobile.linkshorteningservice.exception.KeyGenerationException;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

import static com.example.emobile.linkshorteningservice.util.constant.KeyGenerationConstant.KEY_LENGTH;
import static com.example.emobile.linkshorteningservice.util.constant.KeyGenerationConstant.MAX_ATTEMPTS;

@UtilityClass
public class KeyGenerationUtil {

    public static Mono<String> generateUniqueKey(Function<String, Mono<Boolean>> existenceChecker) {
        return Flux.generate(sink -> sink.next(generateKey()))
                .cast(String.class)
                .concatMap(key -> existenceChecker.apply(key)
                        .filter(exists -> !exists)
                        .map(ignored -> key)
                )
                .take(MAX_ATTEMPTS)
                .next()
                .switchIfEmpty(Mono.error(new KeyGenerationException(
                        "Failed to generate unique key after " + MAX_ATTEMPTS + " attempts")));
    }

    private static String generateKey() {
        return UUID.randomUUID().toString().substring(0, KEY_LENGTH);
    }
}