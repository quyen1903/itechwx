package com.microsoft.itechwx.identity.application.port.out;

import java.util.Optional;

import com.microsoft.itechwx.identity.domain.RefreshToken;

public interface RefreshTokenPort {
    Optional<RefreshToken> findByTokenHashForUpdate(String tokenHash);
}
