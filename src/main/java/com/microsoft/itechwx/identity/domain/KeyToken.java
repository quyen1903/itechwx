package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(name = "auth_id", nullable = false)
    private UUID authId;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public KeyToken(
        UUID id, 
        UUID authId, 
        UUID deviceId, 
        String publicKey, 
        String refreshToken, 
        Boolean isActive,
        Instant createdAt, 
        Instant updatedAt
    ) {
        this.id = id;
        this.authId = authId;
        this.deviceId = deviceId;
        this.publicKey = publicKey;
        this.refreshToken = refreshToken;
        this.isActive = isActive;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

    }   
}