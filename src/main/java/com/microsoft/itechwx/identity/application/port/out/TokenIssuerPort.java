package com.microsoft.itechwx.identity.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface TokenIssuerPort {
    TokenPair issuePair(
        UUID accountId,
        UUID sessionId,
        String email,
        Instant now
    );

    String hashRefreshToken(String refreshToken);
}
