package com.cosmo.cats.integration.security;

import com.cosmo.cats.annotation.WithMockCosmoCatUser;
import com.cosmo.cats.api.dto.ProductSalesReportDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockCosmoCatUser(roles = {"ADMIN"})
    void whenAdminAccessingAdminEndpoint_thenAllowed() throws Exception {
        mockMvc.perform(get("/api/v1/reports/product-sales/top-selling"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCosmoCatUser(roles = {"SELLER"})
    void whenSellerAccessingSellerEndpoint_thenAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCosmoCatUser(roles = {"USER"})
    void whenUserAccessingAdminEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/reports/product-sales/top-selling"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousAccessingProtectedEndpoint_thenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAnonymousAccessingPublicEndpoint_thenAllowed() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCosmoCatUser(roles = {"SELLER"})
    void whenSellerAccessingAdminEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isForbidden());
    }
}
