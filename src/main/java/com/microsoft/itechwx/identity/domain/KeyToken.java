package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.UUID;
import java.util.Objects;

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
public class KeyToken {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "device_session_id",
        nullable = false
    )
    private DeviceSession deviceSession;

    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static KeyToken create(
        UUID id,
        DeviceSession deviceSession,
        String publicKey,
        String refreshTokenHash,
        Instant now
    ) {
        KeyToken token = new KeyToken();

        token.id = Objects.requireNonNull(id);
        token.deviceSession = Objects.requireNonNull(deviceSession);
        token.publicKey = Objects.requireNonNull(publicKey);
        token.refreshTokenHash = Objects.requireNonNull(refreshTokenHash);

        token.isActive = true;
        token.createdAt = Objects.requireNonNull(now);
        token.updatedAt = now;

        return token;
    }
}