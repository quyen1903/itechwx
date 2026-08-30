package com.microsoft.itechwx.identity.adapter.out.security;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.microsoft.itechwx.identity.application.port.out.SigningKeyPort;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;

@Component
public class DatabaseJwtDecoder implements JwtDecoder {
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";

    private final SigningKeyPort signingKeyPort;

    public DatabaseJwtDecoder(SigningKeyPort signingKeyPort) {
        this.signingKeyPort = signingKeyPort;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        SignedJWT unverifiedToken = parse(token);
        if (!JWSAlgorithm.RS256.equals(unverifiedToken.getHeader().getAlgorithm())) {
            throw new JwtException("Unsupported JWT algorithm");
        }

        String kid = unverifiedToken.getHeader().getKeyID();
        if (kid == null || kid.isBlank()) {
            throw new JwtException("JWT kid is required");
        }

        PublicKey publicKey;
        try {
            publicKey = signingKeyPort.getPublicKey(kid);
        } catch (RuntimeException exception) {
            throw new JwtException("JWT signing key is unavailable", exception);
        }
        if (!(publicKey instanceof RSAPublicKey rsaPublicKey)) {
            throw new JwtException("JWT signing key must be RSA");
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withPublicKey(rsaPublicKey)
            .signatureAlgorithm(SignatureAlgorithm.RS256)
            .build();
        Jwt jwt = decoder.decode(token);

        if (!ACCESS_TOKEN_TYPE.equals(jwt.getClaimAsString("type"))) {
            throw new JwtException("JWT is not an access token");
        }
        return jwt;
    }

    private static SignedJWT parse(String token) {
        try {
            return SignedJWT.parse(token);
        } catch (ParseException exception) {
            throw new JwtException("Malformed JWT", exception);
        }
    }
}
