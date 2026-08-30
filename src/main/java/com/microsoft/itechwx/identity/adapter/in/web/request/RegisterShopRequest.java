package com.microsoft.itechwx.identity.adapter.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterShopRequest(
    @NotBlank(message = "Name is required")
    @Size(
        min = 2, 
        max = 50, 
        message = "Name must be between 2 and 50 characters"
    )
    String name,
    String phone,

    @Size(
        max = 200, 
        message = "Address must not exceed 200 characters"
    )
    String address,
    String timezone,
    String language,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    String password,
    String username,

    @NotBlank(message = "Business name is required")
    @Size(
        max = 100,
        message = "Business name must not exceed 100 characters"
    )
    String businessName,

    @Size(
        max = 100, 
        message = "Business type must not exceed 100 characters"
    )
    String businessType,

    @Size(
        max = 50,
        message = "Tax ID must not exceed 50 characters"
    )
    String taxId,
    String currency

) {}
