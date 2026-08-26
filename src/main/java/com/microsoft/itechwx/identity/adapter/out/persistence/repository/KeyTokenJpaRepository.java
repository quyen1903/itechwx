package com.microsoft.itechwx.identity.adapter.out.persistence.repository;

import java.util.UUID;
import com.microsoft.itechwx.identity.domain.KeyToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyTokenJpaRepository extends JpaRepository<KeyToken, UUID>{

}
