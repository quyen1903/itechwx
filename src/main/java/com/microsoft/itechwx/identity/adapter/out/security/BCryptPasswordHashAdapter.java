package com.microsoft.itechwx.identity.adapter.out.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.microsoft.itechwx.identity.application.port.out.PasswordHashPort;

public class BCryptPasswordHashAdapter implements PasswordHashPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHashAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
