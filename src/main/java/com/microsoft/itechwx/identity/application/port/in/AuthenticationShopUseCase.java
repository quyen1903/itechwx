package com.microsoft.itechwx.identity.application.port.in;

import com.microsoft.itechwx.identity.application.contract.command.LoginShopCommand;
import com.microsoft.itechwx.identity.application.contract.command.HandleRefreshToken;
import com.microsoft.itechwx.identity.application.contract.result.TokenPairResult;

public interface AuthenticationShopUseCase {
    TokenPairResult loginShop(LoginShopCommand command);

    TokenPairResult refreshShopToken(HandleRefreshToken command);
}
