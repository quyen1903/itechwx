package com.microsoft.itechwx.identity.application.service;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.RegisterShopResult;
import com.microsoft.itechwx.identity.application.port.in.RegisterShopUseCase;
import com.microsoft.itechwx.identity.domain.enums.AccountType;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

public class RegisterShopService implements RegisterShopUseCase{
    private final CreateAccountUseCase createAccountUseCase;
    private final SaveShopPort saveShopPort;

    @Override
    @Transactional
    public RegisterShopResult register(RegisterShopCommand command) {
        CreatedAccount account = createAccountUseCase.create(
            new CreateAccountCommand(
                command.email(),
                command.password(),
                AccountType.SHOP
            )
        );

        Shop shop = Shop.register(
            UUID.randomUUID(),
            account.accountId(),
            command.businessName(),
            command.businessType(),
            command.taxId(),
            command.phone(),
            command.address(),
            command.currency(),
            command.timezone()
        );

        saveShopPort.save(shop);

        return new RegisterShopResult(
            account.accountId(),
            shop.id(),
            account.normalizedEmail(),
            shop.status()
        );
    }

}
