package com.microsoft.itechwx.identity.adapter.out.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.application.port.out.JwtPublicKeyPort;
import com.microsoft.itechwx.identity.application.port.out.SigningKeyPort;
import com.microsoft.itechwx.identity.application.port.out.model.ActiveSigningKey;
import com.microsoft.itechwx.identity.application.port.out.model.JwtPublicKeyData;

@Component
public class DatabaseSigningKeyAdapter implements SigningKeyPort {
    private static final int RSA_KEY_SIZE = 2048;
    private static final String SIGNING_ALGORITHM = "RS256";

    private final JwtPublicKeyPort publicKeyPort;

    public DatabaseSigningKeyAdapter(JwtPublicKeyPort publicKeyPort) {
        this.publicKeyPort = publicKeyPort;
    }

    @Override
    public ActiveSigningKey createForSession(
        UUID sessionId,
        Instant expiresAt,
        Instant now
    ) {
        if (!expiresAt.isAfter(now)) {
            throw new IllegalArgumentException("Signing key expiry must be in the future");
        }

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(RSA_KEY_SIZE);
            KeyPair pair = generator.generateKeyPair();
            String kid = UUID.randomUUID().toString();

            publicKeyPort.save(new JwtPublicKeyData(
                kid,
                sessionId,
                SIGNING_ALGORITHM,
                pair.getPublic().getEncoded(),
                now,
                expiresAt
            ));

            return new ActiveSigningKey(kid, pair.getPrivate(), pair.getPublic());
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot create JWT signing key", exception);
        }
    }

    @Override
    public PublicKey getPublicKey(String kid) {
        JwtPublicKeyData storedKey = publicKeyPort.findByKid(kid)
            .orElseThrow(() -> new IllegalArgumentException("Unknown signing key"));

        if (!SIGNING_ALGORITHM.equals(storedKey.algorithm())) {
            throw new IllegalArgumentException("Unsupported signing key algorithm");
        }

        try {
            return KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(storedKey.publicKeyDer())
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot decode JWT public key", exception);
        }
    }
}
