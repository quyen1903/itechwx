package com.microsoft.itechwx.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RegistrationRateLimitFilterTest {

    @Test
    void returns429AndRetryAfterOnlyAfterPostLimitIsExceeded() throws Exception {
        RegistrationRateLimitFilter filter = new RegistrationRateLimitFilter(
            new RegistrationRateLimiter(
                2,
                Duration.ofMinutes(1),
                Clock.fixed(Instant.parse("2026-08-24T04:00:00Z"), ZoneOffset.UTC)
            )
        );

        assertThat(invoke(filter, "POST").chainInvoked()).isTrue();
        assertThat(invoke(filter, "POST").chainInvoked()).isTrue();

        FilterResult rejected = invoke(filter, "POST");
        assertThat(rejected.chainInvoked()).isFalse();
        assertThat(rejected.response().getStatus()).isEqualTo(429);
        assertThat(rejected.response().getHeader("Retry-After")).isEqualTo("60");
        assertThat(rejected.response().getContentAsString()).contains("REGISTRATION_RATE_LIMITED");

        assertThat(invoke(filter, "GET").chainInvoked()).isTrue();
    }

    private static FilterResult invoke(
        RegistrationRateLimitFilter filter,
        String method
    ) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, "/");
        request.setServletPath(RegistrationRateLimitFilter.REGISTRATION_PATH);
        request.setRemoteAddr("192.0.2.10");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainInvoked = new AtomicBoolean();

        filter.doFilter(request, response, (ignoredRequest, ignoredResponse) ->
            chainInvoked.set(true)
        );
        return new FilterResult(response, chainInvoked.get());
    }

    private record FilterResult(
        MockHttpServletResponse response,
        boolean chainInvoked
    ) {}
}
