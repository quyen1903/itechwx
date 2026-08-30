package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microsoft.itechwx.identity.adapter.out.persistence.JwtPublicKeyJpaEntity;

public interface JwtPublicKeyJpaRepository
    extends JpaRepository<JwtPublicKeyJpaEntity, String> {
}
