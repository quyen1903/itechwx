package com.microsoft.itechwx.identity.application.contract.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterShopCommand(
    String name,
    String phone,
    String address,
    String timezone,
    String language,
    String email,
    String password,
    String username,
    String businessName,
    String businessType,
    String taxId,
    String currency
) {}
