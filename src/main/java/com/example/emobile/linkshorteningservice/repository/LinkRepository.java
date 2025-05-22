package com.example.emobile.linkshorteningservice.repository;

import com.example.emobile.linkshorteningservice.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {
    Optional<Link> findByShortKey(String shortKey);

    boolean existsByShortKey(String shortKey);
}