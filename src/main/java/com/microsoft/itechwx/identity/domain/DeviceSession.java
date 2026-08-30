package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "account_authentication_id",
        nullable = false
    )
    private AccountAuthentication accountAuthentication;

    @OneToMany(
        mappedBy = "deviceSession",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<RefreshToken> refreshTokens = new ArrayList<>();


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

    public void addRefreshToken( RefreshToken token) {
        refreshTokens.add( Objects.requireNonNull(token));
    }
    public static DeviceSession create(
        UUID id,
        AccountAuthentication authentication,
        UUID deviceId,
        String deviceName,
        Instant now
    ) {
        DeviceSession session = new DeviceSession();
        session.id = Objects.requireNonNull(id);
        session.accountAuthentication = Objects.requireNonNull(authentication);
        session.deviceId = Objects.requireNonNull(deviceId);
        session.deviceName = deviceName; // optional
        session.lastLoginAt = Objects.requireNonNull(now);
        session.isActive = true;
        session.createdAt = now;
        session.updatedAt = now;

        return session;
    }
}