package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountProfile {
    @Id
    @Column(name = "account_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "language")
    private String language;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private AccountProfile(
        Account account,
        String name,
        Instant now
    ) {
        this.account = Objects.requireNonNull(account);
        this.name = Objects.requireNonNull(name);

        this.avatar = null;
        this.phone = null;
        this.address = null;
        this.timezone = null;
        this.language = null;

        this.isActive = true;

        this.createdAt = Objects.requireNonNull(now);
        this.updatedAt = now;
    }

    public static AccountProfile register(
        Account account,
        String name,
        Instant now
    ) {
        return new AccountProfile(
            account,
            name,
            now
        );
    }
}