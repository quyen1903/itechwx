package com.microsoft.itechwx.onboarding.application.port.in;

import com.microsoft.itechwx.onboarding.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.onboarding.application.contract.result.RegisterShopResult;

public interface RegisterShopUseCase {
    RegisterShopResult register(RegisterShopCommand command);
}
