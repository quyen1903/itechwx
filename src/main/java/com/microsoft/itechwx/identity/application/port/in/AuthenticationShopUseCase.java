package com.microsoft.itechwx.identity.application.port.in;

import com.microsoft.itechwx.identity.application.contract.command.LoginShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.LoginShopResult;

public interface AuthenticationShopUseCase {
    LoginShopResult loginShop(LoginShopCommand command);
}
