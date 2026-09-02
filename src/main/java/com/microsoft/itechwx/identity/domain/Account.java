package com.microsoft.itechwx.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AccountType;
import com.microsoft.itechwx.identity.domain.enums.AccountStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Table(name = "accounts")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 32)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(
        mappedBy = "account",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private AccountAuthentication accountAuthentication;

    @OneToOne(        
        mappedBy = "account",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private AccountProfile accountProfile;
    
    private Account(
        UUID id, 
        AccountType accountType,
        Instant now
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.accountType = Objects.requireNonNull(accountType);
        this.status = AccountStatus.ACTIVE;
        this.createdAt = now;
        this.updatedAt = now;

    }

    public static Account registerShop(
        UUID id,
        Instant now
    ){
        return new Account(id, AccountType.SHOP, now);
    }

    public void attachAuthentication( AccountAuthentication authentication ) {
        this.accountAuthentication = Objects.requireNonNull(authentication);
    }

    public void attachProfile(AccountProfile profile){
        this.accountProfile = Objects.requireNonNull(profile);
    }

    public boolean canAuthenticate() {
        return status == AccountStatus.ACTIVE;
    }

}
