package com.microsoft.itechwx.identity.domain;

import java.math.BigInteger;
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
    private BigInteger createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private BigInteger updatedAt;
}
