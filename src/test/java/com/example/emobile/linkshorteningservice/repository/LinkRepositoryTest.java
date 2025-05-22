package com.example.emobile.linkshorteningservice.repository;

import com.example.emobile.linkshorteningservice.model.Link;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

    private Link saveLink(Link testLink) {
        return linkRepository.save(testLink);
    }
}