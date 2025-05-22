package com.example.emobile.linkshorteningservice.controller;

import com.example.emobile.linkshorteningservice.service.LinkService;
import com.example.emobile.linkshorteningservice.util.TestDataBuilderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateShortLinkSuccessfully() throws Exception {
        var validRequest = TestDataBuilderUtil.createValidLinkRequestDto();

        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.shortKey").isNotEmpty())
                .andExpect(jsonPath("$.alias").value(validRequest.alias()))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    public void shouldReturnBadRequestForInvalidInput() throws Exception {
        var invalidRequest = TestDataBuilderUtil.createInvalidLinkRequestDto();

        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(400))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed for fields")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("originalUrl")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("ttlInSeconds")))
                .andExpect(jsonPath("$.path").value("/api/links"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void shouldRedirectToOriginalUrl() throws Exception {
        var validRequest = TestDataBuilderUtil.createValidLinkRequestDto();
        var savedLink = linkService.createShortLink(validRequest);

        mockMvc.perform(get("/api/links/{shortKey}", savedLink.shortKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", validRequest.originalUrl()))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().exists("Expires"));
    }
}