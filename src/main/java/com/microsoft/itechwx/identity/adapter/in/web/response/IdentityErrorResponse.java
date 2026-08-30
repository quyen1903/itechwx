package com.microsoft.itechwx.identity.adapter.in.web.response;

public record IdentityErrorResponse(
    String code,
    String message
) {}
