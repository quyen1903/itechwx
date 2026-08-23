package com.microsoft.itechwx.shop.adapter.out.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ShopMembershipJpaId implements Serializable {

    @Column(name = "shop_id", nullable = false, updatable = false)
    private UUID shopId;

    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    protected ShopMembershipJpaId() {}

    ShopMembershipJpaId(UUID shopId, UUID accountId) {
        this.shopId = shopId;
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ShopMembershipJpaId that)) {
            return false;
        }
        return Objects.equals(shopId, that.shopId)
            && Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopId, accountId);
    }
}
