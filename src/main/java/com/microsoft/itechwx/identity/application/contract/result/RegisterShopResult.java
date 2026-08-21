package com.microsoft.itechwx.identity.application.contract.result;

import java.util.UUID;

public record RegisterShopResult(
    UUID accountId,
    UUID shopId,
    String email,
    String status,
    boolean emailVerificationRequired
) {

}
