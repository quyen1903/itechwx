package com.microsoft.itechwx.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("observability")
@AutoConfigureMockMvc
@Import(ActuatorSecurityTest.TestJwtConfiguration.class)
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:actuator;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ActuatorSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthIsPublicAndDoesNotExposeComponentDetails() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.components").doesNotExist());
    }

    @Test
    void localDiagnosticEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/mappings"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/actuator/mappings").with(jwt()))
            .andExpect(status().isOk());
    }

    @Test
    void sensitiveEnvironmentEndpointIsNotExposed() throws Exception {
        mockMvc.perform(get("/actuator/env").with(jwt()))
            .andExpect(status().isNotFound());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestJwtConfiguration {

        @Bean
        @Primary
        JwtDecoder testJwtDecoder() {
            return ignoredToken -> null;
        }

        @Bean
        @Primary
        Clock testClock() {
            return Clock.systemUTC();
        }
    }
}
