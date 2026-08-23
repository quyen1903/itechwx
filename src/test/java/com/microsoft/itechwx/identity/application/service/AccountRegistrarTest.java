package com.microsoft.itechwx.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.microsoft.itechwx.identity.application.contract.command.CreateAccountCommand;
import com.microsoft.itechwx.identity.application.exception.DuplicateAccountException;
import com.microsoft.itechwx.identity.application.port.out.AccountRepository;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import com.microsoft.itechwx.identity.domain.Account;
import com.microsoft.itechwx.identity.domain.enums.AccountStatus;
import com.microsoft.itechwx.identity.domain.enums.AccountType;

class AccountRegistrarTest {

    private static final Instant NOW = Instant.parse("2026-08-24T03:00:00Z");
    private static final String RAW_PASSWORD = "correct-horse-battery-staple";

    @Test
    void createsAccountAndCredentialWithoutPersistingRawPassword() {
        RecordingAccountRepository repository = new RecordingAccountRepository(false);
        RecordingPasswordHashPort passwordHashPort = new RecordingPasswordHashPort();
        AccountRegistrar registrar = new AccountRegistrar(
            repository,
            passwordHashPort,
            Clock.fixed(NOW, ZoneOffset.UTC)
        );

        var result = registrar.create(
            new CreateAccountCommand(
                "  Shop.Owner@Example.COM ",
                RAW_PASSWORD,
                " ShopOwner ",
                AccountType.SHOP
            )
        );

        Account saved = repository.saved;
        assertThat(passwordHashPort.hashCalls).isEqualTo(1);
        assertThat(saved).isNotNull();
        assertThat(saved.accountType()).isEqualTo(AccountType.SHOP);
        assertThat(saved.status()).isEqualTo(AccountStatus.PENDING_VERIFICATION);
        assertThat(saved.authentication().normalizedEmail()).isEqualTo("shop.owner@example.com");
        assertThat(saved.authentication().normalizedUsername()).isEqualTo("shopowner");
        assertThat(saved.authentication().passwordHash()).isEqualTo("encoded-password-value-123");
        assertThat(saved.authentication().passwordHash()).doesNotContain(RAW_PASSWORD);
        assertThat(saved.createdAt()).isEqualTo(NOW);
        assertThat(result.accountId()).isEqualTo(saved.id());
        assertThat(result.normalizedEmail()).isEqualTo("shop.owner@example.com");
    }

    @Test
    void mapsRepositoryDuplicateAfterHashingWithoutReturningAnAccount() {
        RecordingAccountRepository repository = new RecordingAccountRepository(true);
        RecordingPasswordHashPort passwordHashPort = new RecordingPasswordHashPort();
        AccountRegistrar registrar = new AccountRegistrar(
            repository,
            passwordHashPort,
            Clock.fixed(NOW, ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> registrar.create(
            new CreateAccountCommand(
                "existing@example.com",
                RAW_PASSWORD,
                null,
                AccountType.SHOP
            )
        )).isInstanceOf(DuplicateAccountException.class);

        assertThat(passwordHashPort.hashCalls).isOne();
        assertThat(repository.saveAttempts).isOne();
    }

    @Test
    void rejectsPasswordsThatExceedBcryptUtf8Limit() {
        AccountRegistrar registrar = new AccountRegistrar(
            new RecordingAccountRepository(false),
            new RecordingPasswordHashPort(),
            Clock.fixed(NOW, ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> registrar.create(commandWithPassword("a".repeat(73))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("72 UTF-8 bytes");
        assertThatThrownBy(() -> registrar.create(commandWithPassword("密".repeat(25))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("72 UTF-8 bytes");
    }

    @Test
    void commandStringRedactsPassword() {
        CreateAccountCommand command = new CreateAccountCommand(
            "owner@example.com",
            RAW_PASSWORD,
            null,
            AccountType.SHOP
        );

        assertThat(command.toString())
            .contains("rawPassword=<redacted>")
            .contains("email=<redacted>")
            .doesNotContain(RAW_PASSWORD)
            .doesNotContain("owner@example.com");
    }

    private static CreateAccountCommand commandWithPassword(String password) {
        return new CreateAccountCommand(
            "owner@example.com",
            password,
            null,
            AccountType.SHOP
        );
    }

    private static final class RecordingAccountRepository implements AccountRepository {
        private final boolean duplicate;
        private Account saved;
        private int saveAttempts;

        private RecordingAccountRepository(boolean duplicate) {
            this.duplicate = duplicate;
        }

        @Override
        public Account save(Account account) {
            saveAttempts++;
            if (duplicate) {
                throw new DuplicateAccountException();
            }
            this.saved = account;
            return account;
        }
    }

    private static final class RecordingPasswordHashPort implements PasswordHashPort {
        private int hashCalls;

        @Override
        public String hash(String rawPassword) {
            hashCalls++;
            return "encoded-password-value-123";
        }

        @Override
        public boolean matches(String rawPassword, String hashedPassword) {
            return false;
        }
    }
}
