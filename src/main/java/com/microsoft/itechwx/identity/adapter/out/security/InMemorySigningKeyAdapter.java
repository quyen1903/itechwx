package com.microsoft.itechwx.identity.adapter.out.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Bean;

import java.util.concurrent.ConcurrentHashMap;

import com.microsoft.itechwx.identity.application.port.out.SigningKeyPort;
import com.microsoft.itechwx.identity.application.port.out.model.ActiveSigningKey;

public class InMemorySigningKeyAdapter implements SigningKeyPort{
    private final AtomicReference<ActiveSigningKey> activeKey = new AtomicReference<>();
    private final Map<String, PublicKey> verificationKeys = new ConcurrentHashMap<>();
    
    public InMemorySigningKeyAdapter(){};

    @Override
    public ActiveSigningKey getActiveKey(){
        return activeKey.get();
    };

    @Override
    public PublicKey getPublicKey(String kid){
        PublicKey key = verificationKeys.get(kid);

        if (key == null){
            throw new IllegalArgumentException("Unknown signing key: " + kid);
        }
        return key;
    };

    @Override
    public synchronized void rotate() {

        try {
            KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");

            generator.initialize(3072);

            KeyPair pair = generator.generateKeyPair();

            String kid = UUID.randomUUID().toString();

            ActiveSigningKey newKey =
                new ActiveSigningKey(
                    kid,
                    pair.getPrivate(),
                    pair.getPublic()
                );

            /*
             * Public key cũ vẫn nằm trong verificationKeys
             * => token cũ vẫn verify được.
             */

            verificationKeys.put(
                kid,
                pair.getPublic()
            );

            activeKey.set(newKey);

        } catch (Exception e) {
            throw new IllegalStateException(
                "Cannot generate signing key",
                e
            );
        }
    }


}
