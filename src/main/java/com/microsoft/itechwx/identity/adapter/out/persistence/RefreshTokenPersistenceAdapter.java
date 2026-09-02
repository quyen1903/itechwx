package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import com.microsoft.itechwx.identity.application.port.out.RefreshTokenPort;
import com.microsoft.itechwx.identity.domain.RefreshToken;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenPort {
    private final RefreshTokenJpaRepository repository;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RefreshToken> findByTokenHashForUpdate(String tokenHash) {
        return repository.findByTokenHash(tokenHash);
    }
}
