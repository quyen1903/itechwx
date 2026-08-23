package com.microsoft.itechwx.shop.application.port.in;

import com.microsoft.itechwx.shop.application.contract.command.CreateShopCommand;
import com.microsoft.itechwx.shop.application.contract.result.CreatedShop;

public interface CreateShopUseCase {
    CreatedShop create(CreateShopCommand command);
}
