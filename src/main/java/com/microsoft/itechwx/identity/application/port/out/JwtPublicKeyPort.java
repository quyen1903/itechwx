package com.microsoft.itechwx.identity.application.port.out;

import java.util.Optional;

import com.microsoft.itechwx.identity.application.port.out.model.JwtPublicKeyData;

public interface JwtPublicKeyPort {
    void save(JwtPublicKeyData publicKey);

    Optional<JwtPublicKeyData> findByKid(String kid);
}
