package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record DeviceSession(
    UUID id,
    UUID accountId,
    UUID deviceId,
    String deviceName,
    Instant lastLoginAt,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
