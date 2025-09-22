package com.example.emobile.linkshorteningservice.model.callback;

import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class LinkEntityBeforeConvertCallback implements BeforeConvertCallback<LinkEntity> {

    @Override
    public Publisher<LinkEntity> onBeforeConvert(LinkEntity entity, SqlIdentifier table) {
        var now = OffsetDateTime.now();

        var updatedEntity = entity.toBuilder()
                .id(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : now)
                .updatedAt(now)
                .clickCount(entity.getClickCount() != null ? entity.getClickCount() : 0L)
                .isActive(resolveIsActive(entity, now))
                .build();

        return Mono.just(updatedEntity);
    }

    private Boolean resolveIsActive(LinkEntity entity, OffsetDateTime now) {
        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(now)) {
            return false;
        }

        return entity.getIsActive() != null ? entity.getIsActive() : true;
    }
}