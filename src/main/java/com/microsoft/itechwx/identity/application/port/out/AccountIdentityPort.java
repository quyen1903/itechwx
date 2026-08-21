package com.microsoft.itechwx.identity.application.port.out;

import java.util.Optional;
import com.microsoft.itechwx.identity.domain.AccountAuthentication;

//declares what the application needs
public interface AccountIdentityPort {
    boolean existsByEmail(String normalizedEmail);

    Optional<AccountAuthentication> findByEmail(String normalizedEmail);

    AccountAuthentication save(AccountAuthentication accountAuthentication);
}
