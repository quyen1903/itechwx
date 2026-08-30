package com.microsoft.itechwx.identity.application.contract.command;

public record LoginShopCommand(
    String email,
    String password,
    String deviceName
) {
    @Override
    public String toString() {
        return "LoginShopCommand[email=<redacted>, password=<redacted>, deviceName="
            + deviceName + "]";
    }
}
