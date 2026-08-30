package com.microsoft.itechwx.identity.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.microsoft.itechwx.identity.adapter.in.web.response.IdentityErrorResponse;
import com.microsoft.itechwx.identity.application.exception.DuplicateAccountException;
import com.microsoft.itechwx.identity.application.exception.InvalidCredentialsException;

@RestControllerAdvice(assignableTypes = IdentityController.class)
public class IdentityExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<IdentityErrorResponse> invalidCredentials() {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new IdentityErrorResponse(
                "INVALID_CREDENTIALS",
                "Invalid email or password"
            ));
    }

    @ExceptionHandler(DuplicateAccountException.class)
    ResponseEntity<IdentityErrorResponse> duplicateAccount() {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new IdentityErrorResponse(
                "ACCOUNT_ALREADY_EXISTS",
                "Registration conflicts with existing account data"
            ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<IdentityErrorResponse> invalidRequest(IllegalArgumentException exception) {
        return ResponseEntity
            .badRequest()
            .body(new IdentityErrorResponse("INVALID_REQUEST", exception.getMessage()));
    }
}
