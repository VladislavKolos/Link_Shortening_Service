package com.example.emobile.linkshorteningservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "link")
public class Link extends BaseEntity {

    @NotBlank
    @Size(max = 2048)
    @Column(name = "original_url", length = 2048)
    private String originalUrl;

    @NotBlank
    @Size(max = 255)
    @Column(name = "short_key", unique = true)
    private String shortKey;

    @Size(max = 255)
    @Column(name = "alias", unique = true)
    private String alias;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @NotNull
    @Column(name = "is_active")
    private Boolean isActive;

    @NotNull
    @Column(name = "click_count")
    private Long clickCount;

    @PreUpdate
    @PrePersist
    public void checkExpiration() {
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            isActive = false;
        }
    }
}