package com.microsoft.itechwx.identity.application.contract.command;

import java.time.LocalDate;

public record RegisterCustomerCommand(
       String email,
    String password,
    String name,
    LocalDate dateOfBirth,
    String phone,
    String avatar
) {}
