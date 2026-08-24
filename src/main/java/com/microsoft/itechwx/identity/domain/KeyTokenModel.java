package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "key_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeyTokenModel {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auth_id", nullable = false, updatable = false)
    private AccountAuthenticationModel accountAuthentication;

    @Column(name = "device_id", nullable = false, updatable = false)
    private UUID deviceId;

    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public KeyTokenModel(
        UUID id,
        AccountAuthenticationModel accountAuthentication,
        UUID deviceId,
        String publicKey,
        String refreshTokenHash,
        boolean active,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.accountAuthentication = Objects.requireNonNull(
            accountAuthentication,
            "accountAuthentication must not be null"
        );
        this.deviceId = Objects.requireNonNull(deviceId, "deviceId must not be null");
        this.publicKey = requireText(publicKey, "publicKey");
        this.refreshTokenHash = requireText(refreshTokenHash, "refreshTokenHash");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
