package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AuthMethod;

public final class AccountAuthentication {

    private final UUID id;
    private final UUID accountId;
    private final String normalizedEmail;
    private final String normalizedUsername;
    private final String passwordHash;
    private final AuthMethod authMethod;
    private final Instant emailVerifiedAt;
    private final int failedLoginAttempts;
    private final Instant lastLoginAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    private AccountAuthentication(
        UUID id,
        UUID accountId,
        String normalizedEmail,
        String normalizedUsername,
        String passwordHash,
        AuthMethod authMethod,
        Instant emailVerifiedAt,
        int failedLoginAttempts,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.normalizedEmail = validateNormalizedEmail(normalizedEmail);
        this.normalizedUsername = validateNormalizedUsername(normalizedUsername);
        this.passwordHash = validatePasswordHash(passwordHash);
        this.authMethod = Objects.requireNonNull(authMethod, "authMethod must not be null");
        this.emailVerifiedAt = emailVerifiedAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (failedLoginAttempts < 0) {
            throw new IllegalArgumentException("failedLoginAttempts must not be negative");
        }
    }

    static AccountAuthentication passwordCredential(
        UUID id,
        UUID accountId,
        String normalizedEmail,
        String normalizedUsername,
        String passwordHash,
        Instant now
    ) {
        return new AccountAuthentication(
            id,
            accountId,
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

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }

    private static String validateNormalizedEmail(String value) {
        String email = requireText(value, "normalizedEmail");
        int atIndex = email.indexOf('@');
        if (email.length() > 320
            || atIndex <= 0
            || atIndex != email.lastIndexOf('@')
            || atIndex == email.length() - 1
            || !email.equals(email.strip().toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("normalizedEmail is invalid");
        }
        return email;
    }

    private static String validateNormalizedUsername(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() < 3
            || value.length() > 64
            || !value.matches("[a-z0-9._-]+")
            || !value.equals(value.strip().toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("normalizedUsername is invalid");
        }
        return value;
    }

    private static String validatePasswordHash(String value) {
        String hash = requireText(value, "passwordHash");
        if (hash.length() < 20 || hash.length() > 512) {
            throw new IllegalArgumentException("passwordHash has an invalid length");
        }
        return hash;
    }

    public UUID id() {
        return id;
    }

    public UUID accountId() {
        return accountId;
    }

    public String normalizedEmail() {
        return normalizedEmail;
    }

    public String normalizedUsername() {
        return normalizedUsername;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public AuthMethod authMethod() {
        return authMethod;
    }

    public Instant emailVerifiedAt() {
        return emailVerifiedAt;
    }

    public int failedLoginAttempts() {
        return failedLoginAttempts;
    }

    public Instant lastLoginAt() {
        return lastLoginAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
