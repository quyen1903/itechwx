package com.microsoft.itechwx.identity.application.port.out.model;

import java.time.Instant;
import java.util.UUID;

public record AccountAuthenticationData(
    UUID accountId,
    String email,
    String passwordHash,
    Instant createdAt
) {}
