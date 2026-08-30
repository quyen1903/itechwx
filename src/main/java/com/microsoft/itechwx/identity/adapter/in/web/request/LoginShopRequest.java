package com.microsoft.itechwx.identity.adapter.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginShopRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    @Size(max = 200, message = "Device name must not exceed 200 characters")
    String deviceName
) {
    @Override
    public String toString() {
        return "LoginShopRequest[email=<redacted>, password=<redacted>, deviceName="
            + deviceName + "]";
    }
}
