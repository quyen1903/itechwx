package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AccountStatus;
import com.microsoft.itechwx.identity.domain.enums.AccountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "accounts")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AccountModel {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 32)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(
        mappedBy = "account",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private AccountAuthenticationModel accountAuthentication;

    public AccountModel(
        UUID id,
        AccountType accountType,
        AccountStatus status,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.accountType = Objects.requireNonNull(accountType, "accountType must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt");
        }
    }

    public static AccountModel register(
        UUID accountId,
        AccountType accountType,
        UUID authenticationId,
        String normalizedEmail,
        String normalizedUsername,
        String passwordHash,
        Instant now
    ) {
        AccountModel account = new AccountModel(
            accountId,
            accountType,
            AccountStatus.PENDING_VERIFICATION,
            now,
            now
        );

        account.accountAuthentication = AccountAuthenticationModel.passwordCredential(
            authenticationId,
            account,
            normalizedEmail,
            normalizedUsername,
            passwordHash,
            now
        );

        return account;
    }
}
