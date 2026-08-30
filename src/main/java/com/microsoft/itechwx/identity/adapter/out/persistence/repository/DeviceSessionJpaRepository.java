package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microsoft.itechwx.identity.domain.DeviceSession;

public interface DeviceSessionJpaRepository extends JpaRepository<DeviceSession, UUID>{

}
