package com.microsoft.itechwx.identity.application.contract.result;

import java.util.UUID;

public record RegisterShopResult(
    UUID shopId,
    String accessToken,
    String refreshToken
) {}
