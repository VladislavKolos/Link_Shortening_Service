package com.example.emobile.linkshorteningservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class UriServiceImplTest {

    @InjectMocks
    private UriServiceImpl service;

    @Test
    public void givenValidIdWhenCreateLinkUriThenReturnUriWithCorrectPath() {
        String shortKey = "abc12345";

        var result = service.createLinkUri(shortKey);

        assertThat(result)
                .isNotNull()
                .hasPath("/api/links/" + shortKey);
    }
}