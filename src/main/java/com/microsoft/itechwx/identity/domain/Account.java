package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AccountStatus;
import com.microsoft.itechwx.identity.domain.enums.AccountType;

public final class Account {

    private final UUID id;
    private final AccountType accountType;
    private final AccountStatus status;
    private final AccountAuthentication authentication;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Account(
        UUID id,
        AccountType accountType,
        AccountStatus status,
        AccountAuthentication authentication,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.accountType = Objects.requireNonNull(accountType, "accountType must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.authentication = Objects.requireNonNull(authentication, "authentication must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (!id.equals(authentication.accountId())) {
            throw new IllegalArgumentException("Authentication must belong to the account");
        }
    }

    public static Account register(
        UUID accountId,
        AccountType accountType,
        UUID authenticationId,
        String normalizedEmail,
        String normalizedUsername,
        String passwordHash,
        Instant now
    ) {
        AccountAuthentication authentication = AccountAuthentication.passwordCredential(
            authenticationId,
            accountId,
            normalizedEmail,
            normalizedUsername,
            passwordHash,
            now
        );

        return new Account(
            accountId,
            accountType,
            AccountStatus.PENDING_VERIFICATION,
            authentication,
            now,
            now
        );
    }

    public UUID id() {
        return id;
    }

    public AccountType accountType() {
        return accountType;
    }

    public AccountStatus status() {
        return status;
    }

    public AccountAuthentication authentication() {
        return authentication;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
