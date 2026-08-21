package com.microsoft.itechwx.identity.application.contract.command;

public record CreateAdminCommand(    
    String email,
    String password,
    String name
) {}
