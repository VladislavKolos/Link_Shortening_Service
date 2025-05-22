package com.example.emobile.linkshorteningservice.service.async;

import com.example.emobile.linkshorteningservice.repository.LinkRepository;
import com.example.emobile.linkshorteningservice.util.LinkShorteningServiceConstantUtil;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AsyncClickServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private AsyncClickService asyncClickService;

    @Test
    public void incrementInTransactionShouldIncrementClickCountWhenLinkExists() {
        String testShortKey = "testKey123";
        var testLink = TestDataBuilderUtil.createValidLink();
        Long initialClicks = testLink.getClickCount();

        when(linkRepository.findByShortKey(testShortKey))
                .thenReturn(Optional.of(testLink));

        asyncClickService.incrementInTransaction(testShortKey);

        verify(linkRepository).findByShortKey(testShortKey);
        assertThat(testLink.getClickCount())
                .isEqualTo(initialClicks + LinkShorteningServiceConstantUtil.CLICK_INCREMENT);
    }

    @Test
    public void incrementInTransactionShouldDoNothingWhenLinkNotFound() {
        String nonExistentKey = "missingKey";

        when(linkRepository.findByShortKey(nonExistentKey))
                .thenReturn(Optional.empty());

        asyncClickService.incrementInTransaction(nonExistentKey);

        verify(linkRepository).findByShortKey(nonExistentKey);
        verifyNoMoreInteractions(linkRepository);
    }
}