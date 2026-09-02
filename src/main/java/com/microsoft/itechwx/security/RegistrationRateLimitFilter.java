package com.microsoft.itechwx.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class RegistrationRateLimitFilter extends OncePerRequestFilter {

    static final String REGISTRATION_PATH = "/api/v1/identity/register/shops";
    static final String LOGIN_PATH = "/api/v1/identity/login/shops";
    static final String REFRESH_PATH = "/api/v1/identity/refreshtoken/shops";

    private final RegistrationRateLimiter rateLimiter;

    public RegistrationRateLimitFilter(RegistrationRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equals(request.getMethod())
            || (
                !REGISTRATION_PATH.equals(request.getServletPath())
                && !LOGIN_PATH.equals(request.getServletPath())
                && !REFRESH_PATH.equals(request.getServletPath())
            );
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String clientKey = request.getServletPath() + ":" + request.getRemoteAddr();
        RegistrationRateLimiter.Decision decision = rateLimiter.acquire(clientKey);
        if (decision.allowed()) {
            filterChain.doFilter(request, response);
            return;
        }

        String errorCode;
        String errorMessage;
        if (REGISTRATION_PATH.equals(request.getServletPath())) {
            errorCode = "REGISTRATION_RATE_LIMITED";
            errorMessage = "Too many registration attempts; try again later";
        } else if (LOGIN_PATH.equals(request.getServletPath())) {
            errorCode = "LOGIN_RATE_LIMITED";
            errorMessage = "Too many login attempts; try again later";
        } else {
            errorCode = "REFRESH_RATE_LIMITED";
            errorMessage = "Too many refresh attempts; try again later";
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(decision.retryAfterSeconds()));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            "{\"code\":\"" + errorCode + "\","
                + "\"message\":\"" + errorMessage + "\"}"
        );
    }
}
