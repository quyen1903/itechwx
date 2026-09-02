package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microsoft.itechwx.identity.adapter.out.persistence.JwtPublicKeyJpaEntity;

public interface JwtPublicKeyJpaRepository
    extends JpaRepository<JwtPublicKeyJpaEntity, String> {

    Optional<JwtPublicKeyJpaEntity> findByKidAndDeviceSession_IsActiveTrue(String kid);
}
