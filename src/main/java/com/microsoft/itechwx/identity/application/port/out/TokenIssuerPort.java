package com.microsoft.itechwx.identity.application.port.out;

import java.time.Instant;
import java.util.UUID;

import com.microsoft.itechwx.identity.application.port.out.model.TokenPair;

public interface TokenIssuerPort {
    TokenPair issuePair(
        UUID accountId,
        UUID sessionId,
        String email,
        Instant now
    );
}
