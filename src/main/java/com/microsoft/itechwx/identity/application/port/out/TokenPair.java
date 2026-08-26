package com.microsoft.itechwx.identity.application.port.out;

import java.time.Instant;

public record TokenPair(
    String accessToken,
    String refreshToken,
    Instant accessTokenExpiresAt,
    Instant refreshTokenExpiresAt
) {

}
