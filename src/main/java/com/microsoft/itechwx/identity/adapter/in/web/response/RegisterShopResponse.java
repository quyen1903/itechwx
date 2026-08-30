package com.microsoft.itechwx.identity.adapter.in.web.response;

import java.util.UUID;

public record RegisterShopResponse(
    UUID accountId,
    String accessToken,
    String refreshToken
) {
    @Override
    public String toString() {
        return "RegisterShopResponse[accountId=" + accountId
            + ", accessToken=<redacted>, refreshToken=<redacted>]";
    }
}
