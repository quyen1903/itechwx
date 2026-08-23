package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AuthMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "account_authentications",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_account_authentications_account",
            columnNames = "account_id"
        ),
        @UniqueConstraint(name = "uq_account_authentications_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_account_authentications_username", columnNames = "username")
    }
)
public class AccountAuthenticationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    @Column(name = "email", nullable = false, length = 320)
    private String normalizedEmail;

    @Column(name = "username", length = 64)
    private String normalizedUsername;

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

    protected AccountAuthenticationJpaEntity() {}

}
