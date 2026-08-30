package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.UUID;
import com.microsoft.itechwx.identity.domain.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, UUID>{

}
