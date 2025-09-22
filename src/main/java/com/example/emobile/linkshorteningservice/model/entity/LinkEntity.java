package com.example.emobile.linkshorteningservice.model.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "link")
@SuperBuilder(toBuilder = true)
public class LinkEntity extends BaseEntity {

    @NotBlank
    @Size(max = 2048)
    private String originalUrl;

    @NotBlank
    @Size(max = 255)
    private String shortKey;

    @Size(max = 255)
    private String alias;

    private OffsetDateTime expiresAt;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Long clickCount;
}