package com.microsoft.itechwx.identity.application.port.out.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record JwtPublicKeyData(
    String kid,
    UUID deviceSessionId,
    String algorithm,
    byte[] publicKeyDer,
    Instant createdAt,
    Instant expiresAt
) {
    public JwtPublicKeyData {
        Objects.requireNonNull(kid, "kid must not be null");
        Objects.requireNonNull(deviceSessionId, "deviceSessionId must not be null");
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        publicKeyDer = Objects.requireNonNull(
            publicKeyDer,
            "publicKeyDer must not be null"
        ).clone();
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    @Override
    public byte[] publicKeyDer() {
        return publicKeyDer.clone();
    }
}
