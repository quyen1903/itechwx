package com.microsoft.itechwx.onboarding.adapter.in.web;

public record RegisterShopResponse(
    String status,
    boolean emailVerificationRequired
) {
    private static final String RECEIVED = "REGISTRATION_RECEIVED";

    static RegisterShopResponse received() {
        return new RegisterShopResponse(RECEIVED, true);
    }
}
