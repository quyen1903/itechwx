package com.microsoft.itechwx.shop.application.port.out;

import com.microsoft.itechwx.shop.domain.Shop;

public interface ShopRepository {
    Shop save(Shop shop);
}
