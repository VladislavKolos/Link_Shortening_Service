package com.example.emobile.linkshorteningservice.repository;

import com.example.emobile.linkshorteningservice.model.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<LinkEntity, UUID> {
    Optional<LinkEntity> findByShortKey(String shortKey);

    boolean existsByShortKey(String shortKey);

    boolean existsByAlias(String alias);
}