package com.microsoft.itechwx.identity.application.exception;

public final class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException() {
        super("Registration conflicts with existing account data");
    }

    public DuplicateAccountException(Throwable cause) {
        super("Registration conflicts with existing account data", cause);
    }
}
