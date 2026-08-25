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
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public DeviceSession(
        UUID id, 
        UUID accountId, 
        UUID deviceId, 
        String deviceName, 
        Instant lastLoginAt,
        Boolean isActive, 
        Instant createdAt, 
        Instant updatedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.lastLoginAt = lastLoginAt;
        this.isActive = isActive;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    
}