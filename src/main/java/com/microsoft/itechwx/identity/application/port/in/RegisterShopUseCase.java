package com.microsoft.itechwx.identity.application.port.in;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.RegisterShopResult;

public interface RegisterShopUseCase {
    RegisterShopResult registerShop(RegisterShopCommand command);
}
