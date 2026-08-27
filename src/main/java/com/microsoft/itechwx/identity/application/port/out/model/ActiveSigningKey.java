package com.microsoft.itechwx.identity.application.port.out.model;

import java.security.PrivateKey;
import java.security.PublicKey;

public record ActiveSigningKey(
    String kid,
    PrivateKey privateKey,
    PublicKey publicKey
) {

}
