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
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "device_session_id",
        nullable = false
    )
    private DeviceSession deviceSession;

    @Column(
        name = "token_hash",
        nullable = false,
        unique = true
    )
    private String tokenHash;

    @Column(
        name = "expires_at",
        nullable = false
    )
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(
        name = "created_at",
        nullable = false
    )
    private Instant createdAt;

    public static RefreshToken create(
        UUID id,
        DeviceSession session,
        String tokenHash,
        Instant expiresAt,
        Instant now
    ) {

        RefreshToken token = new RefreshToken();
        token.id = Objects.requireNonNull(id);
        token.deviceSession = Objects.requireNonNull(session);
        token.tokenHash = Objects.requireNonNull(tokenHash);
        token.expiresAt = Objects.requireNonNull(expiresAt);
        token.revokedAt = null;
        token.createdAt = Objects.requireNonNull(now);
        return token;
    }

    public void revoke(Instant now) {
        this.revokedAt = Objects.requireNonNull(now);
    }

    public boolean isActive(Instant now) {
        return revokedAt == null && now.isBefore(expiresAt);
    }
}