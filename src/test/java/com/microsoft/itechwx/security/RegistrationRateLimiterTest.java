package com.microsoft.itechwx.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class RegistrationRateLimiterTest {

    @Test
    void rejectsAttemptsBeyondTheConfiguredClientWindow() {
        RegistrationRateLimiter limiter = new RegistrationRateLimiter(
            2,
            Duration.ofMinutes(1),
            Clock.fixed(Instant.parse("2026-08-24T04:00:00Z"), ZoneOffset.UTC)
        );

        assertThat(limiter.acquire("192.0.2.10").allowed()).isTrue();
        assertThat(limiter.acquire("192.0.2.10").allowed()).isTrue();

        RegistrationRateLimiter.Decision rejected = limiter.acquire("192.0.2.10");
        assertThat(rejected.allowed()).isFalse();
        assertThat(rejected.retryAfterSeconds()).isEqualTo(60);
        assertThat(limiter.acquire("192.0.2.11").allowed()).isTrue();
    }
}
