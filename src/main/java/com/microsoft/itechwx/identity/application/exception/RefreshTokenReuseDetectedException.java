package com.microsoft.itechwx.identity.application.exception;

public final class RefreshTokenReuseDetectedException extends RuntimeException {
    public RefreshTokenReuseDetectedException() {
        super("Refresh token reuse detected; sign in again");
    }
}
