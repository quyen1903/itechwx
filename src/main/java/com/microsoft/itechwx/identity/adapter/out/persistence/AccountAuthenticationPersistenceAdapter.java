package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.adapter.out.persistence.repository.AccountAuthenticationJpaRepository;
import com.microsoft.itechwx.identity.application.port.out.AccountAuthenticationPort;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;

@Component
public class AccountAuthenticationPersistenceAdapter
    implements AccountAuthenticationPort {

    private final AccountAuthenticationJpaRepository repository;

    public AccountAuthenticationPersistenceAdapter(
        AccountAuthenticationJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public AccountAuthentication save(
        AccountAuthentication authentication
    ) {
        return repository.save(authentication);
    }

    @Override
    public Optional<AccountAuthentication> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}