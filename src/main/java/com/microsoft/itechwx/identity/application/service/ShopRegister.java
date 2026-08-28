package com.microsoft.itechwx.identity.application.service;

// import static com.microsoft.itechwx.identity.application.service.AbstractShop.normalizeEmail;
// import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
// import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microsoft.itechwx.identity.application.contract.result.RegisterShopResult;
import com.microsoft.itechwx.identity.application.port.in.RegisterShopUseCase;
import com.microsoft.itechwx.identity.application.port.out.AccountAuthenticationPort;
import com.microsoft.itechwx.identity.application.port.out.AccountPort;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import com.microsoft.itechwx.identity.application.port.out.RefreshTokenHashPort;
import com.microsoft.itechwx.identity.application.port.out.TokenIssuerPort;
import com.microsoft.itechwx.identity.application.port.out.model.TokenPair;
import com.microsoft.itechwx.identity.domain.Account;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;
import com.microsoft.itechwx.identity.domain.AccountProfile;
import com.microsoft.itechwx.identity.domain.DeviceSession;
import com.microsoft.itechwx.identity.domain.RefreshToken;

import jakarta.transaction.Transactional;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;

@Service
public class ShopRegister extends AbstractShop implements RegisterShopUseCase {

    private final AccountPort accountPort;
    private final AccountAuthenticationPort accountAuthenticationPort;
    private final PasswordHashPort passwordHashPort;
    private final Clock clock;
    private final TokenIssuerPort tokenIssuerPort;
    private final RefreshTokenHashPort refreshTokenHashPort;

    public ShopRegister(
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

    @Transactional
    public RegisterShopResult registerShop(
        RegisterShopCommand command
    ) {

        String email = normalizeEmail(command.email());
        if (accountAuthenticationPort.existsByEmail(email)) throw new IllegalArgumentException( "Email already existed");

        Instant now = clock.instant();
        String passwordHash = passwordHashPort.hash(command.password());


        // 1. Account
        Account account = Account.registerShop( UUID.randomUUID(),now);


        // 2. Authentication
        AccountAuthentication authentication = AccountAuthentication.register(
            UUID.randomUUID(),
            account,
            command.username(),
            email,
            passwordHash,
            now
        );

        account.attachAuthentication( authentication );


        // 3. Profile
        AccountProfile profile = AccountProfile.register(
            account,
            command.name(),
            now
        );

        account.attachProfile(profile);


        // 4. Device session
        DeviceSession session = DeviceSession.create(
            UUID.randomUUID(),
            authentication,
            UUID.randomUUID(),
            "macbook M5 promax",
            now
        );

        authentication.addDeviceSession( session );


        // 5. Issue token pair
        TokenPair pair = tokenIssuerPort.issuePair(
            account.getId(),
            session.getId(),
            email,
            now
        );


        // 6. Hash refresh token
        String refreshHash = refreshTokenHashPort.hash( pair.refreshToken());

        // 7. Save refresh credential
        RefreshToken refreshToken =RefreshToken.create(
            UUID.randomUUID(),
            session,
            refreshHash,
            pair.refreshTokenExpiresAt(),
            now
        );

        session.addRefreshToken( refreshToken );

        // 8. Cascade persist whole graph
        accountPort.save(account);


        // 9. Return raw credentials
        return new RegisterShopResult(
            account.getId(),
            pair.accessToken(),
            pair.refreshToken()
        );
    };

}
