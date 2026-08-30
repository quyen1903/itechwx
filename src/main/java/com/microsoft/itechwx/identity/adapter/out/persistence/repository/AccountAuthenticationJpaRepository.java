package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;


public interface AccountAuthenticationJpaRepository extends JpaRepository<AccountAuthentication, UUID> {
    boolean existsByEmail(String email);

    Optional<AccountAuthentication> findByEmail(String email);

}
