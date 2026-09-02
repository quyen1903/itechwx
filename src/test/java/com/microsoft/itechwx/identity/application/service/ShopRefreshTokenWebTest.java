package com.microsoft.itechwx.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.microsoft.itechwx.identity.adapter.out.persistence.repository.DeviceSessionJpaRepository;
import com.microsoft.itechwx.identity.adapter.out.persistence.repository.JwtPublicKeyJpaRepository;
import com.microsoft.itechwx.identity.adapter.out.persistence.repository.RefreshTokenJpaRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:shop-refresh-token;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "itechwx.security.registration-rate-limit.maximum-attempts=10"
})
class ShopRefreshTokenWebTest {
    private static final String REFRESH_PATH = "/api/v1/identity/refreshtoken/shops";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenRepository;

    @Autowired
    private DeviceSessionJpaRepository deviceSessionRepository;

    @Autowired
    private JwtPublicKeyJpaRepository jwtPublicKeyRepository;

    @Test
    void rotatesRefreshTokenOnceAndInvalidatesSessionWhenConsumedTokenIsReused()
        throws Exception {
        mockMvc.perform(post(REFRESH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"unknown-refresh-token\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));

        MvcResult registration = mockMvc.perform(post("/api/v1/identity/register/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Refresh Test Owner",
                      "email": "refresh-web-test@example.invalid",
                      "password": "local-test-password",
                      "username": "refresh_web_test",
                      "businessName": "Refresh Test Shop",
                      "timezone": "UTC",
                      "language": "en",
                      "currency": "USD"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        JsonNode registeredTokens = objectMapper.readTree(
            registration.getResponse().getContentAsString()
        );
        String originalRefreshToken = registeredTokens.get("refreshToken").asText();

        MvcResult refresh = mockMvc.perform(post(REFRESH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshRequest(originalRefreshToken)))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
            .andReturn();

        JsonNode rotatedTokens = objectMapper.readTree(
            refresh.getResponse().getContentAsString()
        );
        String rotatedRefreshToken = rotatedTokens.get("refreshToken").asText();
        String rotatedAccessToken = rotatedTokens.get("accessToken").asText();

        assertThat(rotatedRefreshToken).isNotEqualTo(originalRefreshToken);
        assertThat(refreshTokenRepository.count()).isEqualTo(2);
        assertThat(jwtPublicKeyRepository.count()).isEqualTo(2);
        assertThat(deviceSessionRepository.findAll()).singleElement()
            .satisfies(session -> assertThat(session.isActive()).isTrue());

        mockMvc.perform(post(REFRESH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshRequest(originalRefreshToken)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_REUSED"));

        assertThat(deviceSessionRepository.findAll()).singleElement()
            .satisfies(session -> assertThat(session.isActive()).isFalse());
        assertThat(refreshTokenRepository.findAll())
            .allSatisfy(token -> assertThat(token.wasUsed()).isTrue());

        mockMvc.perform(post(REFRESH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshRequest(rotatedRefreshToken)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));

        mockMvc.perform(get("/actuator/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + rotatedAccessToken))
            .andExpect(status().isUnauthorized());
    }

    private String refreshRequest(String refreshToken) throws Exception {
        return objectMapper.writeValueAsString(
            objectMapper.createObjectNode().put("refreshToken", refreshToken)
        );
    }
}
