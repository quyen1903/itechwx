package com.microsoft.itechwx.identity.application.port.out;

import java.security.PublicKey;

import com.microsoft.itechwx.identity.application.port.out.model.ActiveSigningKey;

public interface SigningKeyPort {
    ActiveSigningKey getActiveKey();

    PublicKey getPublicKey(String kid);

    void rotate();
}