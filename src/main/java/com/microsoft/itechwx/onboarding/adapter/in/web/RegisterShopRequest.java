package com.microsoft.itechwx.onboarding.adapter.in.web;

import com.microsoft.itechwx.onboarding.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.onboarding.adapter.in.web.validation.Utf8Size;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterShopRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    String name,

    @Size(max = 32, message = "Phone must not exceed 32 characters")
    String phone,

    @Size(max = 200, message = "Address must not exceed 200 characters")
    String address,

    @Size(max = 64, message = "Timezone must not exceed 64 characters")
    String timezone,

    @Size(max = 35, message = "Language must not exceed 35 characters")
    String language,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 320, message = "Email must not exceed 320 characters")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 72, message = "Password must be between 12 and 72 characters")
    @Utf8Size(max = 72, message = "Password must not exceed 72 UTF-8 bytes")
    String password,

    @Size(max = 64, message = "Username must not exceed 64 characters")
    @Pattern(
        regexp = "(?:\\s*|[A-Za-z0-9._-]{3,64})",
        message = "Username must be blank or contain 3 to 64 letters, numbers, dots, underscores, or hyphens"
    )
    String username,

    @NotBlank(message = "Business name is required")
    @Size(max = 100, message = "Business name must not exceed 100 characters")
    String businessName,

    @Size(max = 100, message = "Business type must not exceed 100 characters")
    String businessType,

    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    String taxId,

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "(?i)[A-Z]{3}", message = "Currency must be a three-letter ISO code")
    String currency,

    @Size(max = 32, message = "Theme must not exceed 32 characters")
    String theme,

    Boolean emailNotifications,
    Boolean smsNotifications,
    Boolean pushNotifications
) {
    RegisterShopCommand toCommand() {
        return new RegisterShopCommand(
            name,
            phone,
            address,
            defaultIfBlank(timezone, "UTC"),
            defaultIfBlank(language, "en"),
            email,
            password,
            username,
            businessName,
            businessType,
            taxId,
            currency,
            defaultIfBlank(theme, "system"),
            emailNotifications == null || emailNotifications,
            Boolean.TRUE.equals(smsNotifications),
            pushNotifications == null || pushNotifications
        );
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    @Override
    public String toString() {
        return "RegisterShopRequest[name=<redacted>, email=<redacted>"
            + ", password=<redacted>, username=<redacted>"
            + ", businessName=<redacted>]";
    }
}
