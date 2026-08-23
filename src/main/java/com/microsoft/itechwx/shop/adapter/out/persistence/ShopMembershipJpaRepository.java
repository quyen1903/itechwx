package com.microsoft.itechwx.shop.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopMembershipJpaRepository
    extends JpaRepository<ShopMembershipJpaEntity, ShopMembershipJpaId> {}
