package com.microsoft.itechwx.shop.application.contract.result;

import java.util.UUID;

import com.microsoft.itechwx.shop.domain.enums.ShopStatus;

public record CreatedShop(
    UUID shopId,
    UUID ownerAccountId,
    ShopStatus status
) {}
