package com.microsoft.itechwx.identity.adapter.in.web.response;

import java.util.UUID;

public record LoginShopResponse(
    UUID accountId,
    String accessToken,
    String refreshToken
) {
    @Override
    public String toString() {
        return "LoginShopResponse[accountId=" + accountId
            + ", accessToken=<redacted>, refreshToken=<redacted>]";
    }
}
