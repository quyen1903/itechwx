package com.microsoft.itechwx.onboarding.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.microsoft.itechwx.identity.application.exception.DuplicateAccountException;

@RestControllerAdvice(assignableTypes = ShopRegistrationController.class)
public final class RegistrationExceptionHandler {

    @ExceptionHandler(DuplicateAccountException.class)
    ResponseEntity<RegisterShopResponse> handleDuplicateAccount() {
        return ResponseEntity.accepted().body(RegisterShopResponse.received());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<RegistrationErrorResponse> handleInvalidRegistration(
        IllegalArgumentException exception
    ) {
        return ResponseEntity.badRequest().body(
            new RegistrationErrorResponse("INVALID_REGISTRATION", exception.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<RegistrationErrorResponse> handleInvalidRequest(
        MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult().getAllErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("Registration details are invalid");
        return ResponseEntity.badRequest().body(
            new RegistrationErrorResponse("INVALID_REGISTRATION", message)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<RegistrationErrorResponse> handleUnreadableRequest() {
        return ResponseEntity.badRequest().body(
            new RegistrationErrorResponse(
                "INVALID_REGISTRATION",
                "Registration request body is malformed"
            )
        );
    }

    public record RegistrationErrorResponse(String code, String message) {}
}
