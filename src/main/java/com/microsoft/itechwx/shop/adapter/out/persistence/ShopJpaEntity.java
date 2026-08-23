package com.microsoft.itechwx.shop.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import com.microsoft.itechwx.shop.domain.Shop;
import com.microsoft.itechwx.shop.domain.enums.ShopStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shops")
public class ShopJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "business_type", length = 100)
    private String businessType;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "address", length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ShopStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ShopJpaEntity() {}

    private ShopJpaEntity(Shop shop) {
        this.id = shop.id();
        this.contactName = shop.contactName();
        this.businessName = shop.businessName();
        this.businessType = shop.businessType();
        this.taxId = shop.taxId();
        this.phone = shop.phone();
        this.address = shop.address();
        this.status = shop.status();
        this.createdAt = shop.createdAt();
        this.updatedAt = shop.updatedAt();
    }

    static ShopJpaEntity from(Shop shop) {
        return new ShopJpaEntity(shop);
    }
}
