package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microsoft.itechwx.identity.domain.AccountAuthentication;

public interface AccountAuthenticationJpaRepository extends JpaRepository<AccountAuthentication, UUID> {
    Optional <AccountAuthentication> findByEmail(String email);

    boolean existsByEmail(String normalizedEmail);

    AccountAuthentication save(AccountAuthentication accountAuthentication);
}
