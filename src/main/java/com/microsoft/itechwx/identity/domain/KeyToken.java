package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record KeyToken(
    UUID id,
    UUID authenticationId,
    UUID deviceId,
    String publicKey,
    String refreshTokenHash,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {
    @Override
    public String toString() {
        return "KeyToken[id=" + id
            + ", authenticationId=" + authenticationId
            + ", deviceId=" + deviceId
            + ", publicKey=<redacted>, refreshTokenHash=<redacted>"
            + ", active=" + active + "]";
    }
}
