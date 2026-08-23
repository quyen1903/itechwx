package com.microsoft.itechwx.shop.adapter.out.persistence;

import java.time.Instant;

import com.microsoft.itechwx.shop.domain.ShopMembership;
import com.microsoft.itechwx.shop.domain.enums.ShopMembershipStatus;
import com.microsoft.itechwx.shop.domain.enums.ShopRole;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "shop_memberships")
public class ShopMembershipJpaEntity {

    @EmbeddedId
    private ShopMembershipJpaId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    private ShopRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ShopMembershipStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ShopMembershipJpaEntity() {}

    private ShopMembershipJpaEntity(ShopMembership membership) {
        this.id = new ShopMembershipJpaId(membership.shopId(), membership.accountId());
        this.role = membership.role();
        this.status = membership.status();
        this.createdAt = membership.createdAt();
        this.updatedAt = membership.updatedAt();
    }

    static ShopMembershipJpaEntity from(ShopMembership membership) {
        return new ShopMembershipJpaEntity(membership);
    }
}
