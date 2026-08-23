package com.microsoft.itechwx.identity.application.contract.result;

import java.util.UUID;

import com.microsoft.itechwx.identity.domain.enums.AccountStatus;

public record CreatedAccount(
    UUID accountId,
    String normalizedEmail,
    AccountStatus status
) {}
