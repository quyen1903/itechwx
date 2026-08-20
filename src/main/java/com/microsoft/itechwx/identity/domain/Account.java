package com.microsoft.itechwx.identity.domain;

import java.math.BigInteger;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "accounts")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private BigInteger createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private BigInteger updatedAt;
}
