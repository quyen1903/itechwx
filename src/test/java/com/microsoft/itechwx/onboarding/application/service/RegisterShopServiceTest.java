package com.microsoft.itechwx.onboarding.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.microsoft.itechwx.identity.application.contract.result.CreatedAccount;
import com.microsoft.itechwx.identity.application.port.in.CreateAccountUseCase;
import com.microsoft.itechwx.identity.domain.enums.AccountStatus;
import com.microsoft.itechwx.onboarding.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.shop.application.contract.command.CreateShopCommand;
import com.microsoft.itechwx.shop.application.contract.result.CreatedShop;
import com.microsoft.itechwx.shop.application.port.in.CreateShopUseCase;
import com.microsoft.itechwx.shop.application.port.in.ValidateShopRegistrationUseCase;
import com.microsoft.itechwx.shop.domain.enums.ShopStatus;

class RegisterShopServiceTest {

    @Test
    void splitsOnboardingCommandAndLinksShopToCreatedAccount() {
        UUID accountId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();
        AtomicReference<CreateShopCommand> capturedShopCommand = new AtomicReference<>();

        CreateAccountUseCase createAccount = command -> new CreatedAccount(
            accountId,
            "owner@example.com",
            AccountStatus.PENDING_VERIFICATION
        );
        CreateShopUseCase createShop = command -> {
            capturedShopCommand.set(command);
            return new CreatedShop(shopId, command.ownerAccountId(), ShopStatus.PENDING_REVIEW);
        };

        AtomicReference<CreateShopCommand> validatedShopCommand = new AtomicReference<>();
        ValidateShopRegistrationUseCase validateShop = validatedShopCommand::set;
        RegisterShopService service = new RegisterShopService(
            createAccount,
            createShop,
            validateShop
        );
        var result = service.register(command());

        assertThat(validatedShopCommand.get().businessName()).isEqualTo("Example Store");
        assertThat(capturedShopCommand.get().ownerAccountId()).isEqualTo(accountId);
        assertThat(capturedShopCommand.get().businessName()).isEqualTo("Example Store");
        assertThat(capturedShopCommand.get().currency()).isEqualTo("USD");
        assertThat(result.accountId()).isEqualTo(accountId);
        assertThat(result.shopId()).isEqualTo(shopId);
        assertThat(result.email()).isEqualTo("owner@example.com");
        assertThat(result.status()).isEqualTo(ShopStatus.PENDING_REVIEW);
        assertThat(result.emailVerificationRequired()).isTrue();
    }

    @Test
    void doesNotCreateShopWhenIdentityCreationFails() {
        CreateAccountUseCase createAccount = command -> {
            throw new IllegalStateException("identity failed");
        };
        AtomicReference<CreateShopCommand> capturedShopCommand = new AtomicReference<>();
        CreateShopUseCase createShop = command -> {
            capturedShopCommand.set(command);
            throw new AssertionError("shop must not be called");
        };

        RegisterShopService service = new RegisterShopService(
            createAccount,
            createShop,
            ignored -> {}
        );

        assertThatThrownBy(() -> service.register(command()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("identity failed");
        assertThat(capturedShopCommand.get()).isNull();
    }

    @Test
    void validatesShopDetailsBeforeCallingIdentity() {
        AtomicReference<Boolean> identityCalled = new AtomicReference<>(false);
        CreateAccountUseCase createAccount = command -> {
            identityCalled.set(true);
            throw new AssertionError("identity must not be called");
        };
        CreateShopUseCase createShop = command -> {
            throw new AssertionError("shop must not be created");
        };
        ValidateShopRegistrationUseCase validateShop = command -> {
            throw new IllegalArgumentException("invalid shop details");
        };
        RegisterShopService service = new RegisterShopService(
            createAccount,
            createShop,
            validateShop
        );

        assertThatThrownBy(() -> service.register(command()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid shop details");
        assertThat(identityCalled.get()).isFalse();
    }

    private static RegisterShopCommand command() {
        return new RegisterShopCommand(
            "Shop Owner",
            "+12025550123",
            "100 Market Street",
            "UTC",
            "en",
            "owner@example.com",
            "correct-horse-battery-staple",
            "shopowner",
            "Example Store",
            "Retail",
            null,
            "USD",
            "system",
            true,
            false,
            true
        );
    }
}
