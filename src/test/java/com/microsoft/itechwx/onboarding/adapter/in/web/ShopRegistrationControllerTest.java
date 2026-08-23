package com.microsoft.itechwx.onboarding.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.microsoft.itechwx.identity.application.exception.DuplicateAccountException;
import com.microsoft.itechwx.onboarding.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.onboarding.application.contract.result.RegisterShopResult;
import com.microsoft.itechwx.onboarding.application.port.in.RegisterShopUseCase;
import com.microsoft.itechwx.security.SecurityConfiguration;
import com.microsoft.itechwx.shop.domain.enums.ShopStatus;

@WebMvcTest(
    value = ShopRegistrationController.class,
    properties = "itechwx.security.registration-rate-limit.maximum-attempts=100"
)
@Import({SecurityConfiguration.class, RegistrationExceptionHandler.class})
class ShopRegistrationControllerTest {

    private static final String PATH = "/api/v1/identity/register/shops";
    private static final UUID ACCOUNT_ID = UUID.fromString("58ea787b-4c4d-4786-a874-c3b7da8b0774");
    private static final UUID SHOP_ID = UUID.fromString("086f181d-b0e9-492c-b85e-072b7a3296dc");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterShopUseCase registerShopUseCase;

    @BeforeEach
    void stubRegistration() {
        when(registerShopUseCase.register(any(RegisterShopCommand.class))).thenReturn(
            new RegisterShopResult(
                ACCOUNT_ID,
                SHOP_ID,
                "owner@example.com",
                ShopStatus.PENDING_REVIEW,
                true
            )
        );
    }

    @Test
    void allowsAnonymousShopRegistrationWithoutExposingUnverifiedResourceIds() throws Exception {
        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest("correct-horse-battery-staple", "   ")))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.status").value("REGISTRATION_RECEIVED"))
            .andExpect(jsonPath("$.emailVerificationRequired").value(true))
            .andExpect(jsonPath("$.accountId").doesNotExist())
            .andExpect(jsonPath("$.shopId").doesNotExist());
    }

    @Test
    void protectsNeighboringIdentityRoutes() throws Exception {
        mockMvc.perform(get(PATH)).andExpect(status().isUnauthorized());
    }

    @Test
    void returnsStableValidationShapeForPasswordBeyondBcryptByteLimit() throws Exception {
        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest("密".repeat(25), null)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REGISTRATION"))
            .andExpect(jsonPath("$.message").value("Password must not exceed 72 UTF-8 bytes"));
    }

    @Test
    void returnsBadRequestInsteadOfAuthenticationFailureForMalformedJson() throws Exception {
        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not-json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REGISTRATION"))
            .andExpect(jsonPath("$.message").value("Registration request body is malformed"));
    }

    @Test
    void rejectsWhitespaceOnlyUsernameBeyondBoundaryLimit() throws Exception {
        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest("correct-horse-battery-staple", " ".repeat(65))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REGISTRATION"))
            .andExpect(jsonPath("$.message").value("Username must not exceed 64 characters"));
    }

    @Test
    void doesNotRevealWhichAccountIdentifierConflicted() throws Exception {
        when(registerShopUseCase.register(any(RegisterShopCommand.class)))
            .thenThrow(new DuplicateAccountException());

        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest("correct-horse-battery-staple", "shop_owner")))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.status").value("REGISTRATION_RECEIVED"))
            .andExpect(jsonPath("$.emailVerificationRequired").value(true));
    }

    private static String validRequest(String password, String username) {
        String usernameJson = username == null ? "null" : "\"" + username + "\"";
        return """
            {
              "name": "Shop Owner",
              "timezone": "UTC",
              "language": "en",
              "email": "owner@example.com",
              "password": "%s",
              "username": %s,
              "businessName": "Example Store",
              "businessType": "Retail",
              "currency": "USD",
              "theme": "system"
            }
            """.formatted(password, usernameJson);
    }
}
