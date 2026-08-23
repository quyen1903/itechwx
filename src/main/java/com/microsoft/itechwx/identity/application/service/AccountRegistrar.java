package com.microsoft.itechwx.identity.application.service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.itechwx.identity.application.contract.command.CreateAccountCommand;
import com.microsoft.itechwx.identity.application.contract.result.CreatedAccount;
import com.microsoft.itechwx.identity.application.port.in.CreateAccountUseCase;
import com.microsoft.itechwx.identity.application.port.out.AccountRepository;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import com.microsoft.itechwx.identity.domain.Account;

@Service
public class AccountRegistrar implements CreateAccountUseCase {

    private static final int MINIMUM_PASSWORD_LENGTH = 12;
    private static final int MAXIMUM_PASSWORD_BYTES = 72;

    private final AccountRepository accountRepository;
    private final PasswordHashPort passwordHashPort;
    private final Clock clock;

    public AccountRegistrar(
        AccountRepository accountRepository,
        PasswordHashPort passwordHashPort,
        Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.passwordHashPort = passwordHashPort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CreatedAccount create(CreateAccountCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        String normalizedEmail = normalizeRequired(command.email(), "email").toLowerCase(Locale.ROOT);
        String normalizedUsername = normalizeOptional(command.username());
        validatePassword(command.rawPassword());
        Objects.requireNonNull(command.accountType(), "accountType must not be null");

        String passwordHash = passwordHashPort.hash(command.rawPassword());
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalStateException("Password hashing did not produce a value");
        }

        Instant now = clock.instant();
        Account account = Account.register(
            UUID.randomUUID(),
            command.accountType(),
            UUID.randomUUID(),
            normalizedEmail,
            normalizedUsername,
            passwordHash,
            now
        );

        Account saved = accountRepository.save(account);
        return new CreatedAccount(saved.id(), saved.authentication().normalizedEmail(), saved.status());
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
