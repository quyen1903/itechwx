package com.microsoft.itechwx.identity.adapter.in.web.response;

import java.util.UUID;
public record TokenPairResponse(
    UUID accountId,
    String accessToken,
    String refreshToken
) {
    @Override
    public String toString() {
        return "RefreshTokenShopResponse[accountId=" + accountId
            + ", accessToken=<redacted>, refreshToken=<redacted>]";
    }

}
