package com.microsoft.itechwx.identity.application.service;

import java.time.Clock;

import org.springframework.stereotype.Service;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.port.out.AccountIdentityPort;
import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;

@Service
public class AccountRegistrar {

    private final AccountIdentityPort accountIdentityPort;
    private final PasswordHashPort passwordHashPort;
    private final Clock clock;

    public AccountRegistrar(
        AccountIdentityPort accountIdentityPort,
        PasswordHashPort passwordHashPort,
        Clock clock
    ) {
        this.accountIdentityPort = accountIdentityPort;
        this.passwordHashPort = passwordHashPort;
        this.clock = clock;
    }
    public void register(RegisterShopCommand registerShopCommand) {
        // Implement the logic for registering a shop here
        if(accountIdentityPort.existsByEmail(normalizeEmail(registerShopCommand.email()))) {
            throw new IllegalArgumentException("Email already exists");
        }

        String passwordHash = hashPassword(registerShopCommand.password());
        
    }

    public void loginShop(String email, String password) {
        // Implement the logic for logging in a shop here
        
    }

    private String normalizeEmail(String email) {
        if(email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return email.trim().toLowerCase();
    }
}
