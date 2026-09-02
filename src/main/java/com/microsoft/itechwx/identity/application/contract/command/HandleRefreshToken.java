package com.microsoft.itechwx.identity.application.contract.command;

public record HandleRefreshToken(
    String refreshToken
) {
    public HandleRefreshToken {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token must not be blank");
        }
    }

    @Override
    public String toString() {
        return "HandleRefreshToken[refreshToken=<redacted>]";
    }
}
