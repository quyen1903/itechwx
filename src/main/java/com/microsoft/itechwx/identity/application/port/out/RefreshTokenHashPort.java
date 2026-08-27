package com.microsoft.itechwx.identity.application.port.out;

public interface RefreshTokenHashPort {
    String hash(String rawToken);
}
