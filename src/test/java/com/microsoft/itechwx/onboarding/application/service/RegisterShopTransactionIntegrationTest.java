package com.microsoft.itechwx.onboarding.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import com.microsoft.itechwx.identity.application.exception.DuplicateAccountException;
import com.microsoft.itechwx.onboarding.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.onboarding.application.port.in.RegisterShopUseCase;
import com.microsoft.itechwx.shop.adapter.out.persistence.ShopPersistenceAdapter;
import com.microsoft.itechwx.shop.application.port.out.ShopRepository;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:registration;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
    }
)
@Import(RegisterShopTransactionIntegrationTest.RollbackProbeConfiguration.class)
class RegisterShopTransactionIntegrationTest {

    @Autowired
    private RegisterShopUseCase registerShopUseCase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearRegistrationTables() {
        jdbcTemplate.update("DELETE FROM shop_settings");
        jdbcTemplate.update("DELETE FROM shop_memberships");
        jdbcTemplate.update("DELETE FROM shops");
        jdbcTemplate.update("DELETE FROM account_authentications");
        jdbcTemplate.update("DELETE FROM accounts");
    }

    @Test
    void commitsAllFiveMandatoryRows() {
        registerShopUseCase.register(command("USD", "commit@example.com", "commit_owner"));

        assertThat(rowCount("accounts")).isOne();
        assertThat(rowCount("account_authentications")).isOne();
        assertThat(rowCount("shops")).isOne();
        assertThat(rowCount("shop_memberships")).isOne();
        assertThat(rowCount("shop_settings")).isOne();

        String passwordHash = jdbcTemplate.queryForObject(
            "SELECT password_hash FROM account_authentications",
            String.class
        );
        assertThat(passwordHash).isNotEqualTo("correct-horse-battery-staple");
    }

    @Test
    void rollsBackIdentityRowsWhenShopCreationFails() {
        assertThatThrownBy(() -> registerShopUseCase.register(
            command(
                "USD",
                "rollback@example.com",
                "rollback_owner",
                RollbackProbeConfiguration.ROLLBACK_BUSINESS_NAME
            )
        )).isInstanceOf(RollbackProbeException.class);

        assertThat(rowCount("accounts")).isZero();
        assertThat(rowCount("account_authentications")).isZero();
        assertThat(rowCount("shops")).isZero();
        assertThat(rowCount("shop_memberships")).isZero();
        assertThat(rowCount("shop_settings")).isZero();
    }

    @Test
    void duplicateIdentifierLeavesTheOriginalFiveRowsUntouched() {
        registerShopUseCase.register(command("USD", "duplicate@example.com", "first_owner"));

        assertThatThrownBy(() -> registerShopUseCase.register(
            command("USD", "duplicate@example.com", "second_owner")
        )).isInstanceOf(DuplicateAccountException.class);

        assertThat(rowCount("accounts")).isOne();
        assertThat(rowCount("account_authentications")).isOne();
        assertThat(rowCount("shops")).isOne();
        assertThat(rowCount("shop_memberships")).isOne();
        assertThat(rowCount("shop_settings")).isOne();
    }

    @Test
    void invalidShopDataHasSameOutcomeForExistingAndNewIdentifiers() {
        registerShopUseCase.register(command("USD", "existing@example.com", "existing_owner"));

        assertThatThrownBy(() -> registerShopUseCase.register(
            command("ZZZ", "existing@example.com", "existing_owner_2")
        )).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> registerShopUseCase.register(
            command("ZZZ", "new@example.com", "new_owner")
        )).isInstanceOf(IllegalArgumentException.class);

        assertThat(rowCount("accounts")).isOne();
        assertThat(rowCount("account_authentications")).isOne();
        assertThat(rowCount("shops")).isOne();
        assertThat(rowCount("shop_memberships")).isOne();
        assertThat(rowCount("shop_settings")).isOne();
    }

    private int rowCount(String table) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
        return count == null ? 0 : count;
    }

    private static RegisterShopCommand command(
        String currency,
        String email,
        String username
    ) {
        return command(currency, email, username, "Example Store");
    }

    private static RegisterShopCommand command(
        String currency,
        String email,
        String username,
        String businessName
    ) {
        return new RegisterShopCommand(
            "Shop Owner",
            null,
            null,
            "UTC",
            "en",
            email,
            "correct-horse-battery-staple",
            username,
            businessName,
            "Retail",
            null,
            currency,
            "system",
            true,
            false,
            true
        );
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class RollbackProbeConfiguration {

        private static final String ROLLBACK_BUSINESS_NAME = "Rollback Probe Store";

        @Bean
        @Primary
        ShopRepository rollbackProbeShopRepository(ShopPersistenceAdapter delegate) {
            return shop -> {
                var saved = delegate.save(shop);
                if (ROLLBACK_BUSINESS_NAME.equals(saved.businessName())) {
                    throw new RollbackProbeException();
                }
                return saved;
            };
        }
    }

    private static final class RollbackProbeException extends RuntimeException {}
}
