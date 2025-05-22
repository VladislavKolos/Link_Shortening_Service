package com.example.emobile.linkshorteningservice.service.async;

import com.example.emobile.linkshorteningservice.model.Link;
import com.example.emobile.linkshorteningservice.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.emobile.linkshorteningservice.util.LinkShorteningServiceConstantUtil.CLICK_INCREMENT;

@Service
@RequiredArgsConstructor
public class AsyncClickService {
    private final LinkRepository linkRepository;

    @Transactional
    public void incrementInTransaction(String shortKey) {
        linkRepository.findByShortKey(shortKey)
                .ifPresent(this::incrementAndSave);
    }

    private void incrementAndSave(Link link) {
        link.setClickCount(link.getClickCount() + CLICK_INCREMENT);
        linkRepository.save(link);
    }
}