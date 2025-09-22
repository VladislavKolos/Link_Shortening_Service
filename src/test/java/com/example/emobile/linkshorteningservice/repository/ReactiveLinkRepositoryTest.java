package com.example.emobile.linkshorteningservice.repository;

import com.example.emobile.linkshorteningservice.model.callback.LinkEntityBeforeConvertCallback;
import com.example.emobile.linkshorteningservice.model.entity.LinkEntity;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(LinkEntityBeforeConvertCallback.class)
class ReactiveLinkRepositoryTest {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private ReactiveLinkRepository reactiveLinkRepository;

    @AfterEach
    void cleanupDatabase() {
        databaseClient.sql("DELETE FROM link")
                .fetch()
                .rowsUpdated()
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void findByShortKey_whenExists_thenReturnsEntitySuccessfully() {
        var linkToSave = TestDataBuilderUtil.createValidLink();

        Mono<LinkEntity> retrievedLinkEntity = saveLink(linkToSave)
                .flatMap(savedLink -> reactiveLinkRepository.findByShortKey(savedLink.getShortKey()));

        StepVerifier.create(retrievedLinkEntity)
                .assertNext(foundLink -> {
                    assertThat(foundLink.getId()).isNotNull();
                    assertThat(foundLink.getShortKey()).isEqualTo(linkToSave.getShortKey());
                    assertThat(foundLink.getOriginalUrl()).isEqualTo(linkToSave.getOriginalUrl());
                })
                .verifyComplete();
    }

    @Test
    void existsByShortKey_whenExists_thenReturnsTrue() {
        var linkToSave = TestDataBuilderUtil.createValidLink();

        Mono<Boolean> shortKeyExists = saveLink(linkToSave)
                .then(reactiveLinkRepository.existsByShortKey(linkToSave.getShortKey()));

        StepVerifier.create(shortKeyExists)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByShortKey_whenMissing_thenReturnsFalse() {
        String missingKey = "nonexist01";

        StepVerifier.create(reactiveLinkRepository.existsByShortKey(missingKey))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByAlias_whenExists_thenReturnsTrue() {
        String aliasToSave = "alias_exists_1";
        var linkToSave = TestDataBuilderUtil.createValidLink();
        linkToSave.setAlias(aliasToSave);

        Mono<Boolean> aliasExists = saveLink(linkToSave)
                .then(reactiveLinkRepository.existsByAlias(aliasToSave));

        StepVerifier.create(aliasExists)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByAlias_whenMissing_thenReturnsFalse() {
        String missingAlias = "missing_alias";

        StepVerifier.create(reactiveLinkRepository.existsByAlias(missingAlias))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void incrementClickCountByShortKey_whenExists_thenIncrementsSuccessfully() {
        var linkToSave = TestDataBuilderUtil.createValidLink();
        long initialClickCount = linkToSave.getClickCount();
        int expectedUpdatedRowsCount = 1;

        Mono<Void> updateCompletionMono = saveLink(linkToSave)
                .flatMap(savedLink -> reactiveLinkRepository.incrementClickCountByShortKey(savedLink.getShortKey()))
                .doOnNext(updatedRowsCount -> assertThat(updatedRowsCount).isEqualTo(expectedUpdatedRowsCount))
                .then(reactiveLinkRepository.findByShortKey(linkToSave.getShortKey()))
                .doOnNext(linkAfterIncrease -> assertThat(linkAfterIncrease.getClickCount()).isEqualTo(
                        initialClickCount + 1))
                .then();

        StepVerifier.create(updateCompletionMono).verifyComplete();
    }

    @Test
    void incrementClickCountByShortKey_whenMissing_thenCompletesWithoutValue() {
        String missingKey = "missing01";

        StepVerifier.create(reactiveLinkRepository.incrementClickCountByShortKey(missingKey))
                .verifyComplete();
    }

    private Mono<LinkEntity> saveLink(LinkEntity linkEntity) {
        return reactiveLinkRepository.save(linkEntity);
    }
}