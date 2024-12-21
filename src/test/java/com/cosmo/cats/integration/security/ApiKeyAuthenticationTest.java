package com.cosmo.cats.integration.security;

import com.cosmo.cats.persistence.entity.ApiKeyEntity;
import com.cosmo.cats.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiKeyAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiKeyService apiKeyService;

    private ApiKeyEntity validApiKey;
    private ApiKeyEntity expiredApiKey;

    @BeforeEach
    void setUp() {
        validApiKey = apiKeyService.createApiKey(
            "Test Key",
            "For testing",
            Set.of("SELLER"),
            LocalDateTime.now().plusDays(1)
        );

        expiredApiKey = apiKeyService.createApiKey(
            "Expired Key",
            "For testing",
            Set.of("SELLER"),
            LocalDateTime.now().minusDays(1)
        );
    }

    @Test
    void whenUsingValidApiKey_thenAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                .header("X-API-KEY", validApiKey.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void whenUsingExpiredApiKey_thenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                .header("X-API-KEY", expiredApiKey.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoApiKey_thenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAccessingPublicEndpoint_thenAllowed() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }
}
