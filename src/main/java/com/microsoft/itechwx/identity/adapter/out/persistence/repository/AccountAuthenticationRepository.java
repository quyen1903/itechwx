package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microsoft.itechwx.identity.domain.AccountAuthenticationModel;


public interface AccountAuthenticationRepository extends JpaRepository<AccountAuthenticationModel, UUID> {
    boolean existsByEmail(String email);

    Optional<AccountAuthenticationModel> findByEmail(String email);
    
}
