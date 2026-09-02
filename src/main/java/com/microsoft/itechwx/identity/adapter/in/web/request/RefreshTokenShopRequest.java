package com.microsoft.itechwx.identity.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenShopRequest(
    @NotBlank
    String refreshToken
) {
    @Override
    public String toString() {
        return "RefreshTokenShopRequest[refreshToken=<redacted>]";
    }
}
