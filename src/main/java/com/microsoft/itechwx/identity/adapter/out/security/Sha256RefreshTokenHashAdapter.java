package com.microsoft.itechwx.identity.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.application.port.out.RefreshTokenHashPort;

@Component
public class Sha256RefreshTokenHashAdapter implements RefreshTokenHashPort {

    @Override
    public String hash(String rawToken) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                rawToken.getBytes( StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}