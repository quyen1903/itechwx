package com.microsoft.itechwx.identity.application.port.in;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;

public interface RegisterShopUseCase {
    RegisterShopResult register(RegisterShopCommand command);
}
