package com.microsoft.itechwx.shop.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.shop.domain.enums.ShopMembershipStatus;
import com.microsoft.itechwx.shop.domain.enums.ShopRole;

public final class ShopMembership {

    private final UUID shopId;
    private final UUID accountId;
    private final ShopRole role;
    private final ShopMembershipStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private ShopMembership(
        UUID shopId,
        UUID accountId,
        ShopRole role,
        ShopMembershipStatus status,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.shopId = Objects.requireNonNull(shopId, "shopId must not be null");
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    static ShopMembership initialOwner(UUID shopId, UUID accountId, Instant now) {
        return new ShopMembership(
            shopId,
            accountId,
            ShopRole.OWNER,
            ShopMembershipStatus.ACTIVE,
            now,
            now
        );
    }

    public UUID shopId() {
        return shopId;
    }

    public UUID accountId() {
        return accountId;
    }

    public ShopRole role() {
        return role;
    }

    public ShopMembershipStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
