package com.microsoft.itechwx.identity.application.contract.command;

import com.microsoft.itechwx.identity.domain.enums.AccountType;

public record CreateAccountCommand(
    String email,
    String rawPassword,
    String username,
    AccountType accountType
) {
    @Override
    public String toString() {
        return "CreateAccountCommand[email=<redacted>, rawPassword=<redacted>"
            + ", username=<redacted>, accountType=" + accountType + "]";
    }
}
