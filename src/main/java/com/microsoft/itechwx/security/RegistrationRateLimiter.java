package com.microsoft.itechwx.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class RegistrationRateLimiter {

    private static final int MAXIMUM_TRACKED_CLIENTS = 100_000;
    private static final long CLEANUP_FREQUENCY = 1_024;

    private final int maximumAttempts;
    private final Duration windowDuration;
    private final Clock clock;
    private final ConcurrentMap<String, AttemptWindow> windows = new ConcurrentHashMap<>();
    private final AtomicLong requestCount = new AtomicLong();

    public RegistrationRateLimiter(int maximumAttempts, Duration windowDuration, Clock clock) {
        if (maximumAttempts < 1) {
            throw new IllegalArgumentException("maximumAttempts must be positive");
        }
        if (windowDuration == null || windowDuration.isZero() || windowDuration.isNegative()) {
            throw new IllegalArgumentException("windowDuration must be positive");
        }
        this.maximumAttempts = maximumAttempts;
        this.windowDuration = windowDuration;
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Decision acquire(String clientKey) {
        String key = clientKey == null || clientKey.isBlank() ? "unknown" : clientKey;
        Instant now = clock.instant();
        periodicallyRemoveExpiredWindows(now);

        if (!windows.containsKey(key) && windows.size() >= MAXIMUM_TRACKED_CLIENTS) {
            return new Decision(false, Math.max(1, windowDuration.toSeconds()));
        }

        AtomicReference<Decision> decision = new AtomicReference<>();
        windows.compute(key, (ignored, current) -> nextWindow(current, now, decision));
        return decision.get();
    }

    private AttemptWindow nextWindow(
        AttemptWindow current,
        Instant now,
        AtomicReference<Decision> decision
    ) {
        if (current == null || !now.isBefore(current.expiresAt())) {
            decision.set(new Decision(true, 0));
            return new AttemptWindow(1, now.plus(windowDuration));
        }
        if (current.attempts() < maximumAttempts) {
            decision.set(new Decision(true, 0));
            return new AttemptWindow(current.attempts() + 1, current.expiresAt());
        }

        long retryAfterSeconds = Math.max(1, Duration.between(now, current.expiresAt()).toSeconds());
        decision.set(new Decision(false, retryAfterSeconds));
        return current;
    }

    private void periodicallyRemoveExpiredWindows(Instant now) {
        if (requestCount.incrementAndGet() % CLEANUP_FREQUENCY == 0) {
            windows.entrySet().removeIf(entry -> !now.isBefore(entry.getValue().expiresAt()));
        }
    }

    public record Decision(boolean allowed, long retryAfterSeconds) {}

    private record AttemptWindow(int attempts, Instant expiresAt) {}
}
