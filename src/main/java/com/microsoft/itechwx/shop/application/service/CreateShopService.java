package com.microsoft.itechwx.shop.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.itechwx.shop.application.contract.command.CreateShopCommand;
import com.microsoft.itechwx.shop.application.contract.result.CreatedShop;
import com.microsoft.itechwx.shop.application.port.in.CreateShopUseCase;
import com.microsoft.itechwx.shop.application.port.in.ValidateShopRegistrationUseCase;
import com.microsoft.itechwx.shop.application.port.out.ShopRepository;
import com.microsoft.itechwx.shop.domain.Shop;

@Service
public class CreateShopService implements CreateShopUseCase, ValidateShopRegistrationUseCase {

    private final ShopRepository shopRepository;
    private final Clock clock;

    public CreateShopService(ShopRepository shopRepository, Clock clock) {
        this.shopRepository = shopRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CreatedShop create(CreateShopCommand command) {
        Shop shop = newShop(command);
        Shop saved = shopRepository.save(shop);
        return new CreatedShop(
            saved.id(),
            saved.ownerMembership().accountId(),
            saved.status()
        );
    }

    @Override
    public void validate(CreateShopCommand command) {
        newShop(command);
    }

    private Shop newShop(CreateShopCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Objects.requireNonNull(command.ownerAccountId(), "ownerAccountId must not be null");

        Instant now = clock.instant();
        return Shop.register(
            UUID.randomUUID(),
            command.ownerAccountId(),
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
            command.pushNotificationsEnabled(),
            now
        );
    }
}
