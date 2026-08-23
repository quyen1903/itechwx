package com.microsoft.itechwx.onboarding.application.contract.result;

import java.util.UUID;

import com.microsoft.itechwx.shop.domain.enums.ShopStatus;

public record RegisterShopResult(
    UUID accountId,
    UUID shopId,
    String email,
    ShopStatus status,
    boolean emailVerificationRequired
) {}
