package com.microsoft.itechwx.identity.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.adapter.out.persistence.repository.DeviceSessionJpaRepository;
import com.microsoft.itechwx.identity.adapter.out.persistence.repository.JwtPublicKeyJpaRepository;
import com.microsoft.itechwx.identity.application.port.out.JwtPublicKeyPort;
import com.microsoft.itechwx.identity.application.port.out.model.JwtPublicKeyData;
import com.microsoft.itechwx.identity.domain.DeviceSession;

@Component
public class JwtPublicKeyPersistenceAdapter implements JwtPublicKeyPort {
    private final JwtPublicKeyJpaRepository publicKeyRepository;
    private final DeviceSessionJpaRepository deviceSessionRepository;

    public JwtPublicKeyPersistenceAdapter(
        JwtPublicKeyJpaRepository publicKeyRepository,
        DeviceSessionJpaRepository deviceSessionRepository
    ) {
        this.publicKeyRepository = publicKeyRepository;
        this.deviceSessionRepository = deviceSessionRepository;
    }

    @Override
    public void save(JwtPublicKeyData publicKey) {
        DeviceSession session = deviceSessionRepository.getReferenceById(
            publicKey.deviceSessionId()
        );
        publicKeyRepository.save(JwtPublicKeyJpaEntity.create(publicKey, session));
    }

    @Override
    public Optional<JwtPublicKeyData> findByKid(String kid) {
        return publicKeyRepository
            .findByKidAndDeviceSession_IsActiveTrue(kid)
            .map(JwtPublicKeyJpaEntity::toData);
    }
}
