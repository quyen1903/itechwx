package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.util.Optional;

import com.microsoft.itechwx.identity.adapter.out.persistence.repository.AccountAuthenticationRepository;
import com.microsoft.itechwx.identity.application.port.out.AccountAuthenticationPort;
import com.microsoft.itechwx.identity.domain.AccountAuthenticationModel;

public class AccountAuthenticationPersistenceAdapter implements AccountAuthenticationPort{
    private final AccountAuthenticationRepository repository;

    public AccountAuthenticationPersistenceAdapter(
        AccountAuthenticationRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public AccountAuthenticationModel save(
        AccountAuthenticationModel authentication
    ) {
    return repository.save(authentication);
    }

    @Override
    public Optional<AccountAuthenticationModel> findByEmail(String email) {
        return repository
            .findByEmail(email)
            .map(entity -> new AccountAuthenticationModel(
                entity.getAccountId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getCreatedAt()
            ));
    }
}
