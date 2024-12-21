package com.cosmo.cats.controller;

import com.cosmo.cats.exception.FeatureNotAvailableException;
import com.cosmo.cats.model.CosmoCat;
import com.cosmo.cats.service.CosmoCatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CosmoCatController.class)
class CosmoCatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CosmoCatService cosmoCatService;

    @Test
    void getCosmoCats_WhenFeatureEnabled_ShouldReturnCats() throws Exception {
        // Given
        List<CosmoCat> cosmoCats = List.of(
            CosmoCat.builder()
                .id(UUID.randomUUID().toString())
                .name("Stellar")
                .spacesuit("Silver Nebula Suit")
                .planet("Mars")
                .build()
        );
        when(cosmoCatService.getCosmoCats()).thenReturn(cosmoCats);

        // When & Then
        mockMvc.perform(get("/api/v1/cosmo-cats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Stellar"))
            .andExpect(jsonPath("$[0].spacesuit").value("Silver Nebula Suit"))
            .andExpect(jsonPath("$[0].planet").value("Mars"));
    }

    @Test
    void getCosmoCats_WhenFeatureDisabled_ShouldReturnNotFound() throws Exception {
        // Given
        when(cosmoCatService.getCosmoCats())
            .thenThrow(new FeatureNotAvailableException("Feature 'cosmoCats' is not available"));

        // When & Then
        mockMvc.perform(get("/api/v1/cosmo-cats"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Feature Not Available"))
            .andExpect(jsonPath("$.message").value("Feature 'cosmoCats' is not available"));
    }
}
