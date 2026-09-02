package com.microsoft.itechwx.identity.application.contract.result;

import java.util.UUID;

public record TokenPairResult(
    UUID accountId,
    String accessToken,
    String refreshToken
) {

    @Override
    public String toString() {
        return "TokenPairFactory [accountId=" + accountId
            + ", accessToken=<redacted>, refreshToken=<redacted>]";
    }
}
