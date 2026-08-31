package com.microsoft.itechwx.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.microsoft.itechwx.identity.adapter.out.persistence.repository.AccountJpaRepository;
import com.microsoft.itechwx.identity.adapter.out.persistence.repository.DeviceSessionJpaRepository;
import com.microsoft.itechwx.identity.adapter.out.persistence.repository.JwtPublicKeyJpaRepository;
import com.microsoft.itechwx.identity.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.port.in.RegisterShopUseCase;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:shop-register-persistence;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ShopRegisterPersistenceTest {

    @Autowired
    private RegisterShopUseCase registerShopUseCase;

    @Autowired
    private AccountJpaRepository accountRepository;

    @Autowired
    private DeviceSessionJpaRepository deviceSessionRepository;

    @Autowired
    private JwtPublicKeyJpaRepository jwtPublicKeyRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenRepository;

    @Test
    void persistsRegistrationGraphAndTokenCredentialsInOneTransaction() {
        var result = registerShopUseCase.registerShop(new RegisterShopCommand(
            "Local Shop Owner",
            null,
            null,
            "UTC",
            "en",
            "persistence-test@example.invalid",
            "local-test-password",
            "persistence_test",
            "Local Test Shop",
            "Retail",
            null,
            "USD"
        ));

        assertThat(result.accountId()).isNotNull();
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(accountRepository.count()).isEqualTo(1);
        assertThat(deviceSessionRepository.count()).isEqualTo(1);
        assertThat(jwtPublicKeyRepository.count()).isEqualTo(1);
        assertThat(refreshTokenRepository.count()).isEqualTo(1);
    }
}
