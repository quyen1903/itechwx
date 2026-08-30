package com.microsoft.itechwx.identity.application.port.out;

import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;

import com.microsoft.itechwx.identity.application.port.out.model.ActiveSigningKey;

public interface SigningKeyPort {
    ActiveSigningKey createForSession(
        UUID sessionId,
        Instant expiresAt,
        Instant now
    );

    PublicKey getPublicKey(String kid);
}
