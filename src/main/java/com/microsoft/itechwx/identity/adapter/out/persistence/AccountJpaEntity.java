package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.Account;
import com.microsoft.itechwx.identity.domain.enums.AccountStatus;
import com.microsoft.itechwx.identity.domain.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountJpaEntity {

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

    protected AccountJpaEntity() {}

    private AccountJpaEntity(Account account) {
        this.id = account.id();
        this.accountType = account.accountType();
        this.status = account.status();
        this.createdAt = account.createdAt();
        this.updatedAt = account.updatedAt();
    }

    static AccountJpaEntity from(Account account) {
        return new AccountJpaEntity(account);
    }
}
