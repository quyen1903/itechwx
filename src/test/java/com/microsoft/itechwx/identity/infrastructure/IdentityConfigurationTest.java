package com.microsoft.itechwx.identity.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;

class IdentityConfigurationTest {

    @Test
    void exposesPasswordHashPortBackedByADelegatingEncoder() {
        try (var context = new AnnotationConfigApplicationContext(IdentityConfiguration.class)) {
            PasswordHashPort passwordHashPort = context.getBean(PasswordHashPort.class);
            String rawPassword = "correct-horse-battery-staple";

            String encoded = passwordHashPort.hash(rawPassword);

            assertThat(encoded).isNotEqualTo(rawPassword);
            assertThat(passwordHashPort.matches(rawPassword, encoded)).isTrue();
            assertThat(context.getBeansOfType(PasswordHashPort.class)).hasSize(1);
        }
    }
}
