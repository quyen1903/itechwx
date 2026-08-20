package com.microsoft.itechwx.identity.domain;

import java.math.BigInteger;
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
@Table(name = "device_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceSession {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "last_login_at", nullable = false)
    private Instant lastLoginAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private BigInteger createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private BigInteger updatedAt;
}
