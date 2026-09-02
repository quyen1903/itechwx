package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import com.microsoft.itechwx.identity.domain.RefreshToken;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
