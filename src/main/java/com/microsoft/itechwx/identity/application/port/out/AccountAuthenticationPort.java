package com.microsoft.itechwx.identity.application.port.out;

import java.util.Optional;

import com.microsoft.itechwx.identity.domain.AccountAuthentication;

public interface AccountAuthenticationPort {
    boolean existsByEmail(String email);

    AccountAuthentication save(
        AccountAuthentication authentication
    );

    Optional<AccountAuthentication> findByEmail(String email);
}
