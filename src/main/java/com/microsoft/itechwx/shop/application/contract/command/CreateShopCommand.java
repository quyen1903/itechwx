package com.microsoft.itechwx.shop.application.contract.command;

import java.util.UUID;

public record CreateShopCommand(
    UUID ownerAccountId,
    String contactName,
    String businessName,
    String businessType,
    String taxId,
    String phone,
    String address,
    String currency,
    String timezone,
    String language,
    String theme,
    boolean emailNotificationsEnabled,
    boolean smsNotificationsEnabled,
    boolean pushNotificationsEnabled
) {}
