package com.microsoft.itechwx.identity.application.service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microsoft.itechwx.identity.application.contract.result.RegisterShopResult;
import com.microsoft.itechwx.identity.application.port.in.RegisterShopUseCase;
import com.microsoft.itechwx.identity.application.port.out.AccountAuthenticationPort;
import com.microsoft.itechwx.identity.application.port.out.AccountPort;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import com.microsoft.itechwx.identity.application.port.out.TokenIssuerPort;
import com.microsoft.itechwx.identity.application.port.out.TokenPair;
import com.microsoft.itechwx.identity.domain.Account;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;
import com.microsoft.itechwx.identity.domain.AccountProfile;
import com.microsoft.itechwx.identity.domain.DeviceSession;
import com.microsoft.itechwx.identity.domain.KeyToken;

import jakarta.transaction.Transactional;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;

@Service
public class ShopRegister implements RegisterShopUseCase {

    private static final int MINIMUM_PASSWORD_LENGTH = 12;
    private static final int MAXIMUM_PASSWORD_BYTES = 72;

    private final AccountPort accountPort;
    private final AccountAuthenticationPort accountAuthenticationPort;
    private final PasswordHashPort passwordHashPort;
    private final Clock clock;
    private final TokenIssuerPort tokenIssuerPort;

    public ShopRegister(
        AccountPort accountPort,
        AccountAuthenticationPort accountAuthenticationPort,
        PasswordHashPort passwordHashPort,
        Clock clock,
        TokenIssuerPort tokenIssuerPort
    ) {
        this.accountPort = accountPort;
        this.accountAuthenticationPort = accountAuthenticationPort;
        this.passwordHashPort = passwordHashPort;
        this.clock = clock;
        this.tokenIssuerPort = tokenIssuerPort;
    }

    @Transactional
    public RegisterShopResult registerShop(RegisterShopCommand command){
        String email = normalizeEmail(command.email());

        if(accountAuthenticationPort.existsByEmail(command.email())){
            throw new IllegalArgumentException("Email already existed");
        }

        String passwordHash = passwordHashPort.hash(command.password());
        Instant now = clock.instant();

        //1 create account record
        Account account = Account.registerShop(
            UUID.randomUUID(), 
            now
        );

        //2 create authentication record
        AccountAuthentication accountAuthentication = AccountAuthentication.register(
            UUID.randomUUID(), 
            account, 
            command.username(), 
            command.email(), 
            passwordHash, 
            now
        );

        account.attachAuthentication(accountAuthentication);

        //3 create profile record 
        AccountProfile accountProfile = AccountProfile.register(account, command.name(), now);
        account.attachProfile(accountProfile);

        accountPort.save(account);

        //4 device session
        DeviceSession session = DeviceSession.create(
            UUID.randomUUID(), 
            accountAuthentication, 
            UUID.randomUUID(),
            "macbook M5 promax", 
            now
        );

        accountAuthentication.addDeviceSession(session);

        TokenPair tokenPair = tokenIssuerPort.issuePair(
            account.getId(),
            session.getId(),
            email, 
            now
        );
        String refreshtokenHash = tokenIssuerPort.hashRefreshToken(tokenPair.refreshToken());

        //5 keyToken
        KeyToken token =  KeyToken.create(
            UUID.randomUUID(), 
            session, 
            publicKey, 
            refreshtokenHash, 
            now
        );
        session.addKeyToken(token);

    }

    // @Override
    // @Transactional
    // public CreatedAccount create(CreateAccountCommand command) {
    //     Objects.requireNonNull(command, "command must not be null");

    //     String normalizedEmail = normalizeRequired(command.email(), "email").toLowerCase(Locale.ROOT);
    //     String normalizedUsername = normalizeOptional(command.username());
    //     validatePassword(command.rawPassword());
    //     Objects.requireNonNull(command.accountType(), "accountType must not be null");

    //     String passwordHash = passwordHashPort.hash(command.rawPassword());
    //     if (passwordHash == null || passwordHash.isBlank()) {
    //         throw new IllegalStateException("Password hashing did not produce a value");
    //     }

    //     Instant now = clock.instant();
    //     AccountModel account = AccountModel.register(
    //         UUID.randomUUID(),
    //         command.accountType(),
    //         UUID.randomUUID(),
    //         normalizedEmail,
    //         normalizedUsername,
    //         passwordHash,
    //         now
    //     );

    //     AccountModel saved = accountRepository.save(account);
    //     return new CreatedAccount(saved.id(), saved.authentication().normalizedEmail(), saved.status());
    // }

    private static String normalizeEmail(String email) {
        if(email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return email.trim().toLowerCase();
    }


    private static String normalizeRequired(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.strip();
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip().toLowerCase(Locale.ROOT);
    }

    private static void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                "Password must contain at least " + MINIMUM_PASSWORD_LENGTH + " characters"
            );
        }
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > MAXIMUM_PASSWORD_BYTES) {
            throw new IllegalArgumentException(
                "Password must not exceed " + MAXIMUM_PASSWORD_BYTES + " UTF-8 bytes"
            );
        }
    }
}
