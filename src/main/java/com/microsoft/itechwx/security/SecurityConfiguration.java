package com.microsoft.itechwx.security;

import java.time.Clock;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import jakarta.servlet.DispatcherType;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain applicationSecurity(
        HttpSecurity http,
        JwtDecoder jwtDecoder
    ) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                RegistrationRateLimitFilter.REGISTRATION_PATH,
                RegistrationRateLimitFilter.LOGIN_PATH
            ))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(
                    HttpMethod.POST,
                    RegistrationRateLimitFilter.REGISTRATION_PATH,
                    RegistrationRateLimitFilter.LOGIN_PATH
                )
                    .permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(resourceServer ->
                resourceServer.jwt(jwt -> jwt.decoder(jwtDecoder))
            );

        return http.build();
    }

    @Bean
    RegistrationRateLimiter registrationRateLimiter(
        @Value("${itechwx.security.registration-rate-limit.maximum-attempts:5}")
        int maximumAttempts,
        @Value("${itechwx.security.registration-rate-limit.window:PT1M}") Duration window
    ) {
        return new RegistrationRateLimiter(maximumAttempts, window, Clock.systemUTC());
    }

    @Bean
    RegistrationRateLimitFilter registrationRateLimitFilter(
        RegistrationRateLimiter registrationRateLimiter
    ) {
        return new RegistrationRateLimitFilter(registrationRateLimiter);
    }
}
