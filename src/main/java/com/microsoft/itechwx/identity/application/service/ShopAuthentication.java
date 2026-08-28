package com.microsoft.itechwx.identity.application.service;

import java.time.Clock;
import java.util.Optional;
import java.lang.IllegalArgumentException;

import com.microsoft.itechwx.identity.application.contract.command.LoginShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.LoginShopResult;
import com.microsoft.itechwx.identity.application.port.in.AuthenticationShopUseCase;
import com.microsoft.itechwx.identity.application.port.out.AccountAuthenticationPort;
import com.microsoft.itechwx.identity.application.port.out.AccountPort;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import com.microsoft.itechwx.identity.application.port.out.RefreshTokenHashPort;
import com.microsoft.itechwx.identity.application.port.out.TokenIssuerPort;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;

public class ShopAuthentication extends AbstractShop implements AuthenticationShopUseCase {
    private final AccountPort accountPort;
    private final AccountAuthenticationPort accountAuthenticationPort;
    private final PasswordHashPort passwordHashPort;
    private final Clock clock;
    private final TokenIssuerPort tokenIssuerPort;
    private final RefreshTokenHashPort refreshTokenHashPort;

    public ShopAuthentication(
        AccountPort accountPort,
        AccountAuthenticationPort accountAuthenticationPort,
        PasswordHashPort passwordHashPort,
        Clock clock,
        TokenIssuerPort tokenIssuerPort,
        RefreshTokenHashPort refreshTokenHashPort
    ) {
        this.accountPort = accountPort;
        this.accountAuthenticationPort = accountAuthenticationPort;
        this.passwordHashPort = passwordHashPort;
        this.clock = clock;
        this.tokenIssuerPort = tokenIssuerPort;
        this.refreshTokenHashPort = refreshTokenHashPort;
    }
    public LoginShopResult loginShop(LoginShopCommand command){
        String email = normalizeEmail(command.email());
        if (!accountAuthenticationPort.existsByEmail(email)) throw new IllegalArgumentException( "Email not found");

        AccountAuthentication accountAuthentication = accountAuthenticationPort
            .findByEmail(email)
            .orElseThrow(()-> new IllegalArgumentException("Invalid email or password"));

        Boolean passwordMatch = passwordHashPort.matches(command.password(), accountAuthentication.getPasswordHash());

        
        return LoginShopResult result;
    }

}
