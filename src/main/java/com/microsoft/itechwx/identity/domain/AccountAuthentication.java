package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AuthMethod;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_authentications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountAuthentication {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(
        name = "account_id", 
        nullable = false, 
        unique = true
    )
    private Account account; 

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method", nullable = false)
    private AuthMethod authMethod = AuthMethod.EMAIL_PASSWORD;

    @Column(name = "last_login_at")
    private Instant  lastLoginAt;

    @Column(name = "login_attempts", nullable = false)
    private Integer loginAttempts;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(        
        mappedBy = "accountAuthentication",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<DeviceSession> deviceSessions = new ArrayList<>();

    private AccountAuthentication(
        UUID id,
        Account account,
        String username,
        String email,
        String passwordHash,
        AuthMethod authMethod,
        Instant now
    ) {
        this.id = Objects.requireNonNull(id);
        this.account = Objects.requireNonNull(account);
        this.username = username;
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.authMethod = Objects.requireNonNull(authMethod);

        this.lastLoginAt = null;
        this.loginAttempts = 0;
        this.isActive = true;
        this.createdAt = Objects.requireNonNull(now);
        this.updatedAt = now;
    }

    public static AccountAuthentication register(
        UUID id,
        Account account,
        String username,
        String email,
        String passwordHash,
        Instant now
    ){
        return new AccountAuthentication(id, account, username, email, passwordHash, AuthMethod.EMAIL_PASSWORD,now);
    }

    public void addDeviceSession(DeviceSession session) {
        this.deviceSessions.add( Objects.requireNonNull(session) );
    }

}