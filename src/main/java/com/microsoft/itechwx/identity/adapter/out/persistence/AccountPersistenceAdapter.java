package com.microsoft.itechwx.identity.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.application.exception.DuplicateAccountException;
import com.microsoft.itechwx.identity.application.port.out.AccountRepository;
import com.microsoft.itechwx.identity.domain.Account;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;

import jakarta.persistence.EntityManager;

@Component
public final class AccountPersistenceAdapter implements AccountRepository {

    private static final String INSERT_AUTHENTICATION = """
        INSERT INTO account_authentications (
            id,
            account_id,
            email,
            username,
            password_hash,
            auth_method,
            email_verified_at,
            failed_login_attempts,
            last_login_at,
            created_at,
            updated_at
        ) VALUES (
            :id,
            :accountId,
            :email,
            :username,
            :passwordHash,
            'EMAIL_PASSWORD',
            NULL,
            0,
            NULL,
            :createdAt,
            :updatedAt
        )
        ON CONFLICT DO NOTHING
        """;

    private final AccountJpaRepository accountJpaRepository;
    private final EntityManager entityManager;

    public AccountPersistenceAdapter(
        AccountJpaRepository accountJpaRepository,
        EntityManager entityManager
    ) {
        this.accountJpaRepository = accountJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Account save(Account account) {
        accountJpaRepository.saveAndFlush(AccountJpaEntity.from(account));
        int insertedRows = insertAuthentication(account.authentication());
        if (insertedRows == 0) {
            throw new DuplicateAccountException();
        }
        if (insertedRows != 1) {
            throw new IllegalStateException("Unexpected authentication insert row count");
        }
        return account;
    }

    private int insertAuthentication(AccountAuthentication authentication) {
        return entityManager.createNativeQuery(INSERT_AUTHENTICATION)
            .setParameter("id", authentication.id())
            .setParameter("accountId", authentication.accountId())
            .setParameter("email", authentication.normalizedEmail())
            .setParameter("username", authentication.normalizedUsername())
            .setParameter("passwordHash", authentication.passwordHash())
            .setParameter("createdAt", authentication.createdAt())
            .setParameter("updatedAt", authentication.updatedAt())
            .executeUpdate();
    }
}
