package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.application.port.out.AccountIdentityPort;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;

//this class implement the ports that the application needs, 
// and it uses the JPA repository to interact with the database
@Component
public class AccountPersistenceAdapter implements AccountIdentityPort {
    private final AccountAuthenticationJpaRepository accountAuthenticationJpaRepository;

    public AccountPersistenceAdapter(AccountAuthenticationJpaRepository accountAuthenticationJpaRepository) {
        this.accountAuthenticationJpaRepository = accountAuthenticationJpaRepository;
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return accountAuthenticationJpaRepository.existsByEmail(normalizedEmail);
    }

    @Override
    public Optional<AccountAuthentication> findByEmail(String email) {
        return accountAuthenticationJpaRepository.findByEmail(email);
    }

    @Override
    public AccountAuthentication save(AccountAuthentication accountAuthentication) {
        return accountAuthenticationJpaRepository.save(accountAuthentication);
    }

}
