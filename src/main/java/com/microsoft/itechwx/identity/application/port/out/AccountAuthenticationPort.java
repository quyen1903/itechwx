package com.microsoft.itechwx.identity.application.port.out;

import java.util.Optional;

import com.microsoft.itechwx.identity.domain.AccountAuthenticationModel;

public interface AccountAuthenticationPort {
    boolean existsByEmail(String email);

    AccountAuthenticationModel save(
        AccountAuthenticationModel authentication
    );

    Optional<AccountAuthenticationModel> findByEmail(String email);
}
