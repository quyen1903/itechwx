package com.microsoft.itechwx.identity.application.port.in;

import com.microsoft.itechwx.identity.application.contract.command.CreateAccountCommand;
import com.microsoft.itechwx.identity.application.contract.result.LoginShopResult;

public interface AuthenticationShopUseCase {
    LoginShopResult loginShop(CreateAccountCommand command);
    // CreatedAccount refreshTokenShop(CreateAccountCommand command);
    // CreatedAccount logoutShop(CreateAccountCommand command);

}
