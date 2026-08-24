package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AuthMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true, updatable = false)
    private AccountModel account;

    @Column(name = "username", unique = true, length = 64)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 512)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method", nullable = false, length = 32)
    private AuthMethod authMethod;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AccountAuthenticationModel(
        UUID id,
        AccountModel account,
        String email,
        String username,
        String passwordHash,
        AuthMethod authMethod,
        Instant emailVerifiedAt,
        int failedLoginAttempts,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.account = Objects.requireNonNull(account, "account must not be null");
        this.email = requireNormalizedEmail(email);
        this.username = requireNormalizedUsername(username);
        this.passwordHash = requirePasswordHash(passwordHash);
        this.authMethod = Objects.requireNonNull(authMethod, "authMethod must not be null");
        this.emailVerifiedAt = emailVerifiedAt;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (failedLoginAttempts < 0) {
            throw new IllegalArgumentException("failedLoginAttempts must not be negative");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt");
        }

        this.failedLoginAttempts = failedLoginAttempts;
    }

    public static AccountAuthenticationModel passwordCredential(
        UUID id,
        AccountModel account,
        String normalizedEmail,
        String normalizedUsername,
        String passwordHash,
        Instant now
    ) {
        return new AccountAuthenticationModel(
            id,
            account,
            normalizedEmail,
            normalizedUsername,
            passwordHash,
            AuthMethod.EMAIL_PASSWORD,
            null,
            0,
            null,
            now,
            now
        );
    }

    public UUID getAccountId() {
        return account.getId();
    }

    private static String requireNormalizedEmail(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }

        String email = value.strip();
        if (email.length() > 320
            || !email.equals(email.toLowerCase(Locale.ROOT))
            || email.indexOf('@') <= 0
            || email.indexOf('@') != email.lastIndexOf('@')
            || email.endsWith("@")) {
            throw new IllegalArgumentException("email must be normalized and valid");
        }
        return email;
    }

    private static String requireNormalizedUsername(String value) {
        if (value == null) {
            return null;
        }

        String username = value.strip();
        if (username.length() < 3
            || username.length() > 64
            || !username.equals(username.toLowerCase(Locale.ROOT))
            || !username.matches("[a-z0-9._-]+")) {
            throw new IllegalArgumentException("username must be normalized and valid");
        }
        return username;
    }

    private static String requirePasswordHash(String value) {
        if (value == null || value.length() < 20 || value.length() > 512) {
            throw new IllegalArgumentException("passwordHash has an invalid length");
        }
        return value;
    }
}
