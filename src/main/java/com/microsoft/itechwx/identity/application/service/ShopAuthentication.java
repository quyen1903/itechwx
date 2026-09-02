package com.microsoft.itechwx.identity.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import com.microsoft.itechwx.identity.application.contract.command.HandleRefreshToken;
import org.springframework.stereotype.Service;

import com.microsoft.itechwx.identity.application.contract.command.LoginShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.TokenPairResult;
import com.microsoft.itechwx.identity.application.exception.InvalidCredentialsException;
import com.microsoft.itechwx.identity.application.exception.InvalidRefreshTokenException;
import com.microsoft.itechwx.identity.application.exception.RefreshTokenReuseDetectedException;
import com.microsoft.itechwx.identity.application.port.in.AuthenticationShopUseCase;
import com.microsoft.itechwx.identity.application.port.out.AccountAuthenticationPort;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import com.microsoft.itechwx.identity.application.port.out.RefreshTokenHashPort;
import com.microsoft.itechwx.identity.application.port.out.RefreshTokenPort;
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
    private final RefreshTokenPort refreshTokenPort;

    public ShopAuthentication(
        AccountAuthenticationPort accountAuthenticationPort,
        PasswordHashPort passwordHashPort,
        Clock clock,
        TokenIssuerPort tokenIssuerPort,
        RefreshTokenHashPort refreshTokenHashPort,
        RefreshTokenPort refreshTokenPort
    ) {
        this.accountAuthenticationPort = accountAuthenticationPort;
        this.passwordHashPort = passwordHashPort;
        this.clock = clock;
        this.tokenIssuerPort = tokenIssuerPort;
        this.refreshTokenHashPort = refreshTokenHashPort;
        this.refreshTokenPort = refreshTokenPort;
    }
    @Override
    @Transactional
    public TokenPairResult loginShop(LoginShopCommand command) {
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

        return new TokenPairResult(
            account.getId(),
            pair.accessToken(),
            pair.refreshToken()
        );
    }

    @Override
    @Transactional(dontRollbackOn = {
        RefreshTokenReuseDetectedException.class,
        InvalidRefreshTokenException.class
    })
    public TokenPairResult refreshShopToken(HandleRefreshToken command) {
        String tokenHash = refreshTokenHashPort.hash(command.refreshToken());
        RefreshToken currentToken = refreshTokenPort
            .findByTokenHashForUpdate(tokenHash)
            .orElseThrow(InvalidRefreshTokenException::new);
        DeviceSession session = currentToken.getDeviceSession();
        Instant now = clock.instant();

        if (!session.isActive()) {
            throw new InvalidRefreshTokenException();
        }
        if (currentToken.wasUsed()) {
            session.deactivate(now);
            throw new RefreshTokenReuseDetectedException();
        }
        if (currentToken.isExpired(now)) {
            session.deactivate(now);
            throw new InvalidRefreshTokenException();
        }

        AccountAuthentication authentication = session.getAccountAuthentication();
        Account account = authentication.getAccount();
        if (account.getAccountType() != AccountType.SHOP || !account.canAuthenticate()) {
            session.deactivate(now);
            throw new InvalidRefreshTokenException();
        }

        currentToken.revoke(now);
        TokenPair pair = tokenIssuerPort.issuePair(
            account.getId(),
            session.getId(),
            authentication.getEmail(),
            now
        );

        RefreshToken replacement = RefreshToken.create(
            UUID.randomUUID(),
            session,
            refreshTokenHashPort.hash(pair.refreshToken()),
            pair.refreshTokenExpiresAt(),
            now
        );
        session.addRefreshToken(replacement);

        return new TokenPairResult(
            account.getId(),
            pair.accessToken(),
            pair.refreshToken()
        );
    }


    private static InvalidCredentialsException invalidCredentials() {
        return new InvalidCredentialsException();
    }
}
