package com.microsoft.itechwx.identity.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microsoft.itechwx.identity.application.contract.command.LoginShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.LoginShopResult;
import com.microsoft.itechwx.identity.application.exception.InvalidCredentialsException;
import com.microsoft.itechwx.identity.application.port.in.AuthenticationShopUseCase;
import com.microsoft.itechwx.identity.application.port.out.AccountAuthenticationPort;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import com.microsoft.itechwx.identity.application.port.out.RefreshTokenHashPort;
import com.microsoft.itechwx.identity.application.port.out.TokenIssuerPort;
import com.microsoft.itechwx.identity.application.port.out.model.TokenPair;
import com.microsoft.itechwx.identity.domain.Account;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;
import com.microsoft.itechwx.identity.domain.DeviceSession;
import com.microsoft.itechwx.identity.domain.RefreshToken;
import com.microsoft.itechwx.identity.domain.enums.AccountType;

import jakarta.transaction.Transactional;

@Service
public class ShopAuthentication extends AbstractShop implements AuthenticationShopUseCase {
    private final AccountAuthenticationPort accountAuthenticationPort;
    private final PasswordHashPort passwordHashPort;
    private final Clock clock;
    private final TokenIssuerPort tokenIssuerPort;
    private final RefreshTokenHashPort refreshTokenHashPort;

    public ShopAuthentication(
        AccountAuthenticationPort accountAuthenticationPort,
        PasswordHashPort passwordHashPort,
        Clock clock,
        TokenIssuerPort tokenIssuerPort,
        RefreshTokenHashPort refreshTokenHashPort
    ) {
        this.accountAuthenticationPort = accountAuthenticationPort;
        this.passwordHashPort = passwordHashPort;
        this.clock = clock;
        this.tokenIssuerPort = tokenIssuerPort;
        this.refreshTokenHashPort = refreshTokenHashPort;
    }
    @Override
    @Transactional
    public LoginShopResult loginShop(LoginShopCommand command) {
        String email = normalizeEmail(command.email());
        AccountAuthentication accountAuthentication = accountAuthenticationPort
            .findByEmail(email)
            .orElseThrow(ShopAuthentication::invalidCredentials);

        Account account = accountAuthentication.getAccount();
        if (account.getAccountType() != AccountType.SHOP || !account.canAuthenticate()) {
            throw invalidCredentials();
        }

        boolean passwordMatch = passwordHashPort.matches(
            command.password(),
            accountAuthentication.getPasswordHash()
        );
        if (!passwordMatch) {
            throw invalidCredentials();
        }

        Instant now = clock.instant();
        accountAuthentication.recordSuccessfulLogin(now);

        DeviceSession session = DeviceSession.create(
            UUID.randomUUID(),
            accountAuthentication,
            UUID.randomUUID(),
            normalizeOptional(command.deviceName()),
            now
        );
        accountAuthentication.addDeviceSession(session);
        accountAuthenticationPort.save(accountAuthentication);

        TokenPair pair = tokenIssuerPort.issuePair(
            account.getId(),
            session.getId(),
            email,
            now
        );

        RefreshToken refreshToken = RefreshToken.create(
            UUID.randomUUID(),
            session,
            refreshTokenHashPort.hash(pair.refreshToken()),
            pair.refreshTokenExpiresAt(),
            now
        );
        session.addRefreshToken(refreshToken);
        accountAuthenticationPort.save(accountAuthentication);

        return new LoginShopResult(
            account.getId(),
            pair.accessToken(),
            pair.refreshToken()
        );
    }

    private static InvalidCredentialsException invalidCredentials() {
        return new InvalidCredentialsException();
    }
}
