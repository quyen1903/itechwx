package com.microsoft.itechwx.identity.domain;

import java.math.BigInteger;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AuthMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_authentications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountAuthenticationModel {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;    

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method", nullable = false)
    private AuthMethod authMethod = AuthMethod.EMAIL_PASSWORD;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "last_login_at")
    private BigInteger lastLoginAt;

    @Column(name = "login_attempts", nullable = false)
    private Integer loginAttempts;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private BigInteger createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private BigInteger updatedAt;

    public AccountAuthenticationModel(
        UUID accountId,
        String email,
        String passwordHash,
        BigInteger createdAt
    ) {
        this.accountId = accountId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }
}