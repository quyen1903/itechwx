package com.microsoft.itechwx.shop.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.microsoft.itechwx.shop.application.contract.command.CreateShopCommand;
import com.microsoft.itechwx.shop.application.port.out.ShopRepository;
import com.microsoft.itechwx.shop.domain.Shop;
import com.microsoft.itechwx.shop.domain.enums.ShopMembershipStatus;
import com.microsoft.itechwx.shop.domain.enums.ShopRole;
import com.microsoft.itechwx.shop.domain.enums.ShopStatus;

class CreateShopServiceTest {

    @Test
    void createsShopOwnerMembershipAndSettingsAsOneAggregate() {
        UUID accountId = UUID.randomUUID();
        RecordingShopRepository repository = new RecordingShopRepository();
        Instant now = Instant.parse("2026-08-24T03:00:00Z");
        CreateShopService service = new CreateShopService(
            repository,
            Clock.fixed(now, ZoneOffset.UTC)
        );

        var result = service.create(new CreateShopCommand(
            accountId,
            "Shop Owner",
            "Example Store",
            "Retail",
            null,
            null,
            null,
            "usd",
            "UTC",
            "en",
            "System",
            true,
            false,
            true
        ));

        Shop saved = repository.saved;
        assertThat(saved.status()).isEqualTo(ShopStatus.PENDING_REVIEW);
        assertThat(saved.ownerMembership().accountId()).isEqualTo(accountId);
        assertThat(saved.ownerMembership().role()).isEqualTo(ShopRole.OWNER);
        assertThat(saved.ownerMembership().status()).isEqualTo(ShopMembershipStatus.ACTIVE);
        assertThat(saved.settings().currency()).isEqualTo("USD");
        assertThat(saved.settings().timezone()).isEqualTo("UTC");
        assertThat(saved.settings().theme()).isEqualTo("system");
        assertThat(saved.createdAt()).isEqualTo(now);
        assertThat(result.shopId()).isEqualTo(saved.id());
    }

    private static final class RecordingShopRepository implements ShopRepository {
        private Shop saved;

        @Override
        public Shop save(Shop shop) {
            this.saved = shop;
            return shop;
        }
    }
}
