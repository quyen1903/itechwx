package com.microsoft.itechwx.identity.application.contract.result;

import java.util.UUID;

public record LoginShopResult(
    UUID accountId,
    String accessToken,
    String refreshToken
) {
    @Override
    public String toString() {
        return "LoginShopResult[accountId=" + accountId
            + ", accessToken=<redacted>, refreshToken=<redacted>]";
    }
}
