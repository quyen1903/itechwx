package com.microsoft.itechwx.identity.application.contract.result;

import java.util.UUID;

public record RegisterShopResult(
    UUID accountId,
    String accessToken,
    String refreshToken
) {}
