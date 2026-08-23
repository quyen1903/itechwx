package com.microsoft.itechwx.onboarding.application.contract.command;

public record RegisterShopCommand(
    String contactName,
    String phone,
    String address,
    String timezone,
    String language,
    String email,
    String rawPassword,
    String username,
    String businessName,
    String businessType,
    String taxId,
    String currency,
    String theme,
    boolean emailNotificationsEnabled,
    boolean smsNotificationsEnabled,
    boolean pushNotificationsEnabled
) {
    @Override
    public String toString() {
        return "RegisterShopCommand[contactName=<redacted>, email=<redacted>"
            + ", rawPassword=<redacted>, username=<redacted>"
            + ", businessName=<redacted>]";
    }
}
