package com.microsoft.itechwx.identity.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.adapter.out.persistence.repository.AccountJpaRepository;
import com.microsoft.itechwx.identity.application.port.out.AccountPort;
import com.microsoft.itechwx.identity.domain.Account;

@Component
public class AccountPersistenceAdapter implements AccountPort{
    private final AccountJpaRepository repository;

    public AccountPersistenceAdapter( AccountJpaRepository repository ) {
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        return repository.save(account);
    }

}
