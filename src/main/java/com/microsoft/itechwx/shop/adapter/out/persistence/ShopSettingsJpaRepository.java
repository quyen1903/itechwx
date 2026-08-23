package com.microsoft.itechwx.shop.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopSettingsJpaRepository extends JpaRepository<ShopSettingsJpaEntity, UUID> {}
