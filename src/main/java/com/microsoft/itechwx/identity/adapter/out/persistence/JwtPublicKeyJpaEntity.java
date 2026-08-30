package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.time.Instant;

import com.microsoft.itechwx.identity.application.port.out.model.JwtPublicKeyData;
import com.microsoft.itechwx.identity.domain.DeviceSession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jwt_public_keys")
public class JwtPublicKeyJpaEntity {
    @Id
    @Column(name = "kid", nullable = false, length = 36)
    private String kid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_session_id", nullable = false)
    private DeviceSession deviceSession;

    @Column(name = "algorithm", nullable = false, length = 16)
    private String algorithm;

    @Lob
    @Column(name = "public_key_der", nullable = false)
    private byte[] publicKeyDer;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    protected JwtPublicKeyJpaEntity() {}

    public static JwtPublicKeyJpaEntity create(
        JwtPublicKeyData data,
        DeviceSession deviceSession
    ) {
        JwtPublicKeyJpaEntity entity = new JwtPublicKeyJpaEntity();
        entity.kid = data.kid();
        entity.deviceSession = deviceSession;
        entity.algorithm = data.algorithm();
        entity.publicKeyDer = data.publicKeyDer();
        entity.createdAt = data.createdAt();
        entity.expiresAt = data.expiresAt();
        return entity;
    }

    public JwtPublicKeyData toData() {
        return new JwtPublicKeyData(
            kid,
            deviceSession.getId(),
            algorithm,
            publicKeyDer,
            createdAt,
            expiresAt
        );
    }
}
