package com.microsoft.itechwx.shop.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.shop.application.port.out.ShopRepository;
import com.microsoft.itechwx.shop.domain.Shop;

@Component
public final class ShopPersistenceAdapter implements ShopRepository {

    private final ShopJpaRepository shopJpaRepository;
    private final ShopMembershipJpaRepository membershipJpaRepository;
    private final ShopSettingsJpaRepository settingsJpaRepository;

    public ShopPersistenceAdapter(
        ShopJpaRepository shopJpaRepository,
        ShopMembershipJpaRepository membershipJpaRepository,
        ShopSettingsJpaRepository settingsJpaRepository
    ) {
        this.shopJpaRepository = shopJpaRepository;
        this.membershipJpaRepository = membershipJpaRepository;
        this.settingsJpaRepository = settingsJpaRepository;
    }

    @Override
    public Shop save(Shop shop) {
        shopJpaRepository.saveAndFlush(ShopJpaEntity.from(shop));
        membershipJpaRepository.save(ShopMembershipJpaEntity.from(shop.ownerMembership()));
        settingsJpaRepository.saveAndFlush(ShopSettingsJpaEntity.from(shop.settings()));
        return shop;
    }
}
