package com.microsoft.itechwx.identity.application.contract.command;

import java.time.LocalDate;

public record RegisterCustomerCommand(
    String email,
    String password,
    String name,
    LocalDate dateOfBirth,
    String phone,
    String avatar
) {
    @Override
    public String toString() {
        return "RegisterCustomerCommand[email=<redacted>, password=<redacted>"
            + ", name=<redacted>, dateOfBirth=<redacted>, phone=<redacted>"
            + ", avatar=<redacted>]";
    }
}
