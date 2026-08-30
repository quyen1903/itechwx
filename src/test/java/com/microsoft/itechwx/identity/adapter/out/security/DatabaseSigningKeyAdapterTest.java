package com.microsoft.itechwx.identity.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.microsoft.itechwx.identity.application.port.out.JwtPublicKeyPort;
import com.microsoft.itechwx.identity.application.port.out.model.JwtPublicKeyData;
import com.microsoft.itechwx.identity.application.port.out.model.TokenPair;
import com.nimbusds.jwt.SignedJWT;

class DatabaseSigningKeyAdapterTest {

    @Test
    void createsAndPersistsANewPublicKeyForEveryTokenPair() throws Exception {
        InMemoryPublicKeyPort publicKeyPort = new InMemoryPublicKeyPort();
        DatabaseSigningKeyAdapter signingKeyAdapter =
            new DatabaseSigningKeyAdapter(publicKeyPort);
        JwtTokenIssuerAdapter tokenIssuer = new JwtTokenIssuerAdapter(signingKeyAdapter);
        DatabaseJwtDecoder decoder = new DatabaseJwtDecoder(signingKeyAdapter);
        UUID accountId = UUID.randomUUID();
        Instant now = Instant.now();

        TokenPair first = tokenIssuer.issuePair(
            accountId,
            UUID.randomUUID(),
            "shop@example.invalid",
            now
        );
        TokenPair second = tokenIssuer.issuePair(
            accountId,
            UUID.randomUUID(),
            "shop@example.invalid",
            now.plusSeconds(1)
        );

        String firstKid = SignedJWT.parse(first.accessToken()).getHeader().getKeyID();
        String secondKid = SignedJWT.parse(second.accessToken()).getHeader().getKeyID();

        assertThat(firstKid).isNotEqualTo(secondKid);
        assertThat(first.refreshToken()).isNotEqualTo(second.refreshToken());
        assertThat(publicKeyPort.keys).containsOnlyKeys(firstKid, secondKid);
        assertThat(publicKeyPort.keys.get(firstKid).publicKeyDer()).isNotEmpty();
        assertThat(decoder.decode(first.accessToken()).getSubject())
            .isEqualTo(accountId.toString());
        assertThat(decoder.decode(second.accessToken()).getSubject())
            .isEqualTo(accountId.toString());
    }

    private static final class InMemoryPublicKeyPort implements JwtPublicKeyPort {
        private final Map<String, JwtPublicKeyData> keys = new HashMap<>();

        @Override
        public void save(JwtPublicKeyData publicKey) {
            keys.put(publicKey.kid(), publicKey);
        }

        @Override
        public Optional<JwtPublicKeyData> findByKid(String kid) {
            return Optional.ofNullable(keys.get(kid));
        }
    }
}
