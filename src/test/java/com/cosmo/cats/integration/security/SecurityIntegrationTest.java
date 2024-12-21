package com.cosmo.cats.integration.security;

import com.cosmo.cats.api.dto.ProductSalesReportDto;
import com.cosmo.cats.config.WireMockConfig;
import com.cosmo.cats.util.JwtTestUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({WireMockConfig.class, JwtTestUtil.class})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    @BeforeEach
    void setUp() {
        wireMockServer.stubFor(
            WireMock.get(urlPathMatching("/realms/cosmo-cats/protocol/openid-connect/certs"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("jwks.json"))
        );
    }

    @Test
    void whenAnonymousAccessingPublicEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }

    @Test
    void whenAnonymousAccessingProtectedEndpoint_thenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenSellerAccessingSellerEndpoint_thenSuccess() throws Exception {
        String token = jwtTestUtil.createSellerToken();

        mockMvc.perform(post("/api/v1/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserAccessingAdminEndpoint_thenForbidden() throws Exception {
        String token = jwtTestUtil.createUserToken();

        mockMvc.perform(get("/api/v1/reports/product-sales/top-selling")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAdminAccessingAdminEndpoint_thenSuccess() throws Exception {
        String token = jwtTestUtil.createAdminToken();

        mockMvc.perform(get("/api/v1/reports/product-sales/top-selling")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
