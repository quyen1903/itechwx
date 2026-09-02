package com.microsoft.itechwx.identity.application.port.in;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.TokenPairResult;

public interface RegisterShopUseCase {
    TokenPairResult registerShop(RegisterShopCommand command);
}
