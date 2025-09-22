package com.example.emobile.linkshorteningservice.repository;

import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReactiveLinkRepository extends ReactiveCrudRepository<LinkEntity, UUID> {

    Mono<LinkEntity> findByShortKey(String shortKey);

    Mono<Boolean> existsByShortKey(String shortKey);

    Mono<Boolean> existsByAlias(String alias);

    @Query("UPDATE link SET click_count = click_count + 1 WHERE short_key = :shortKey")
    Mono<Integer> incrementClickCountByShortKey(String shortKey);
}