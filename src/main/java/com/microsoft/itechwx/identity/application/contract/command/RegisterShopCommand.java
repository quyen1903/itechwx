package com.microsoft.itechwx.identity.application.contract.command;

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
) {
    @Override
    public String toString() {
        return "RegisterShopCommand[fields=<redacted>]";
    }
}
