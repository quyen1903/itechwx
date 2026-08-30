package com.microsoft.itechwx.identity.application.port.out.model;

import java.time.Instant;

public record TokenPair(
    String accessToken,
    String refreshToken,
    Instant accessTokenExpiresAt,
    Instant refreshTokenExpiresAt
) {
    @Override
    public String toString() {
        return "TokenPair[accessToken=<redacted>, refreshToken=<redacted>"
            + ", accessTokenExpiresAt=" + accessTokenExpiresAt
            + ", refreshTokenExpiresAt=" + refreshTokenExpiresAt + "]";
    }
}
