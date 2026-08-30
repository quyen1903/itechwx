package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microsoft.itechwx.identity.domain.AccountProfile;

public interface AccountProfileJpaRepository extends JpaRepository<AccountProfile, UUID>{

}
