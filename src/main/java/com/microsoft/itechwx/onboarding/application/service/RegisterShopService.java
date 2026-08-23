package com.microsoft.itechwx.onboarding.application.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.itechwx.identity.application.contract.command.CreateAccountCommand;
import com.microsoft.itechwx.identity.application.contract.result.CreatedAccount;
import com.microsoft.itechwx.identity.application.port.in.CreateAccountUseCase;
import com.microsoft.itechwx.identity.domain.enums.AccountType;
import com.microsoft.itechwx.onboarding.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.onboarding.application.contract.result.RegisterShopResult;
import com.microsoft.itechwx.onboarding.application.port.in.RegisterShopUseCase;
import com.microsoft.itechwx.shop.application.contract.command.CreateShopCommand;
import com.microsoft.itechwx.shop.application.contract.result.CreatedShop;
import com.microsoft.itechwx.shop.application.port.in.CreateShopUseCase;
import com.microsoft.itechwx.shop.application.port.in.ValidateShopRegistrationUseCase;

@Service
public class RegisterShopService implements RegisterShopUseCase {

    private final CreateAccountUseCase createAccountUseCase;
    private final CreateShopUseCase createShopUseCase;
    private final ValidateShopRegistrationUseCase validateShopRegistrationUseCase;

    public RegisterShopService(
        CreateAccountUseCase createAccountUseCase,
        CreateShopUseCase createShopUseCase,
        ValidateShopRegistrationUseCase validateShopRegistrationUseCase
    ) {
        this.createAccountUseCase = createAccountUseCase;
        this.createShopUseCase = createShopUseCase;
        this.validateShopRegistrationUseCase = validateShopRegistrationUseCase;
    }

    @Override
    @Transactional
    public RegisterShopResult register(RegisterShopCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        validateShopRegistrationUseCase.validate(toCreateShopCommand(command, UUID.randomUUID()));

        CreatedAccount account = createAccountUseCase.create(
            new CreateAccountCommand(
                command.email(),
                command.rawPassword(),
                command.username(),
                AccountType.SHOP
            )
        );

        CreatedShop shop = createShopUseCase.create(
            toCreateShopCommand(command, account.accountId())
        );

        if (!account.accountId().equals(shop.ownerAccountId())) {
            throw new IllegalStateException("Created shop owner does not match the registered account");
        }

        return new RegisterShopResult(
            account.accountId(),
            shop.shopId(),
            account.normalizedEmail(),
            shop.status(),
            true
        );
    }

    private static CreateShopCommand toCreateShopCommand(
        RegisterShopCommand command,
        UUID ownerAccountId
    ) {
        return new CreateShopCommand(
            ownerAccountId,
            command.contactName(),
            command.businessName(),
            command.businessType(),
            command.taxId(),
            command.phone(),
            command.address(),
            command.currency(),
            command.timezone(),
            command.language(),
            command.theme(),
            command.emailNotificationsEnabled(),
            command.smsNotificationsEnabled(),
            command.pushNotificationsEnabled()
        );
    }
}
