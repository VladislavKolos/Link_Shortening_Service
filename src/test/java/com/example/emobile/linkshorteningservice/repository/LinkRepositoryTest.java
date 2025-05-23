package com.example.emobile.linkshorteningservice.repository;

import com.example.emobile.linkshorteningservice.model.Link;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LinkRepositoryTest {

    @Autowired
    private LinkRepository linkRepository;

    @Test
    public void shouldSaveAndRetrieveLinkByShortKey() {
        var link = saveLink(TestDataBuilderUtil.createValidLink());

        Optional<Link> retrieved = linkRepository.findByShortKey(link.getShortKey());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getOriginalUrl()).isEqualTo(link.getOriginalUrl());
    }

    @Test
    public void shouldReturnTrueWhenShortKeyExists() {
        var link = saveLink(TestDataBuilderUtil.createValidLink());

        boolean exists = linkRepository.existsByShortKey(link.getShortKey());

        assertThat(exists).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenShortKeyDoesNotExist() {
        String nonExistingKey = "nonexistent";

        boolean exists = linkRepository.existsByShortKey(nonExistingKey);

        assertThat(exists).isFalse();
    }

    @Test
    public void shouldIncrementClickCountAndUpdateVersion() {
        var link = saveLink(TestDataBuilderUtil.createValidLink());
        var initialVersion = link.getVersion();

        link.setClickCount(link.getClickCount() + 1);
        var updated = linkRepository.saveAndFlush(link);

        assertThat(updated.getClickCount()).isEqualTo(1L);
        assertThat(updated.getVersion()).isGreaterThan(initialVersion);
    }

    @Test
    public void shouldSetIsActiveFalseForExpiredLink() {
        var expired = TestDataBuilderUtil.createExpiredLink();

        var saved = saveLink(expired);

        assertThat(saved.getIsActive()).isFalse();
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void shouldThrowOptimisticLockingFailureExceptionWhenUpdatingStaleEntity() {
        var persistedLink = saveLink(TestDataBuilderUtil.createValidLink());

        var upToDateCopy = linkRepository.findById(persistedLink.getId()).orElseThrow();
        var staleCopy = linkRepository.findById(persistedLink.getId()).orElseThrow();

        upToDateCopy.setClickCount(upToDateCopy.getClickCount() + 1);
        linkRepository.save(upToDateCopy);

        staleCopy.setClickCount(staleCopy.getClickCount() + 1);

        assertThatThrownBy(() -> linkRepository.save(staleCopy))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    private Link saveLink(Link testLink) {
        return linkRepository.save(testLink);
    }
}