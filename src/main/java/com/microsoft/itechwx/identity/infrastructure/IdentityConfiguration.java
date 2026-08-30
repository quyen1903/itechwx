package com.microsoft.itechwx.identity.infrastructure;

import java.time.Clock;

import com.microsoft.itechwx.identity.adapter.out.security.BCryptPasswordHashAdapter;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class IdentityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    Clock clock(){
        return Clock.systemUTC();
    }

    @Bean
    PasswordHashPort passwordHashPort(PasswordEncoder passwordEncoder){
        return new BCryptPasswordHashAdapter(passwordEncoder);
    }
}
