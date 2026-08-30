package com.microsoft.itechwx.identity.adapter.out.security;

import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.application.port.out.SigningKeyPort;
import com.microsoft.itechwx.identity.application.port.out.TokenIssuerPort;
import com.microsoft.itechwx.identity.application.port.out.model.ActiveSigningKey;
import com.microsoft.itechwx.identity.application.port.out.model.TokenPair;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * ConcurentHashMap
 */
@Component
public class JwtTokenIssuerAdapter implements TokenIssuerPort {

    private static final Duration ACCESS_TTL =
        Duration.ofMinutes(15);

    private static final Duration REFRESH_TTL =
        Duration.ofDays(30);

    private final SigningKeyPort signingKeyPort;

    private final SecureRandom secureRandom =
        new SecureRandom();

    public JwtTokenIssuerAdapter(
        SigningKeyPort signingKeyPort
    ) {
        this.signingKeyPort = signingKeyPort;
    }

    @Override
    public TokenPair issuePair(
        UUID accountId,
        UUID sessionId,
        String email,
        Instant now
    ) {

        Instant accessExpiresAt =
            now.plus(ACCESS_TTL);

        Instant refreshExpiresAt =
            now.plus(REFRESH_TTL);

        ActiveSigningKey key = signingKeyPort.createForSession(
            sessionId,
            accessExpiresAt,
            now
        );

        String accessToken =
            createAccessToken(
                accountId,
                sessionId,
                email,
                key,
                now,
                accessExpiresAt
            );

        String refreshToken =
            generateRefreshToken();

        return new TokenPair(
            accessToken,
            refreshToken,
            accessExpiresAt,
            refreshExpiresAt
        );
    }

    private String generateRefreshToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }

    private String createAccessToken(
        UUID accountId,
        UUID sessionId,
        String email,
        ActiveSigningKey key,
        Instant issuedAt,
        Instant expiresAt
    ) {

        try {
            JWTClaimsSet claims =
                new JWTClaimsSet.Builder()
                    .subject(accountId.toString())
                    .claim(
                        "sid",
                        sessionId.toString()
                    )
                    .claim("email", email)
                    .claim("type", "ACCESS")
                    .issueTime(
                        Date.from(issuedAt)
                    )
                    .expirationTime(
                        Date.from(expiresAt)
                    )
                    .build();

            JWSHeader header =
                new JWSHeader.Builder(
                    JWSAlgorithm.RS256
                )
                    .keyID(key.kid())
                    .build();

            SignedJWT jwt =
                new SignedJWT(
                    header,
                    claims
                );

            jwt.sign(
                new RSASSASigner(
                    (RSAPrivateKey)
                        key.privateKey()
                )
            );

            return jwt.serialize();

        } catch (Exception e) {
            throw new IllegalStateException(
                "Cannot issue access token",
                e
            );
        }
    }
}
