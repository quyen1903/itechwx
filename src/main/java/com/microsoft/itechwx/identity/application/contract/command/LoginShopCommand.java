package com.microsoft.itechwx.identity.application.contract.command;

public record LoginShopCommand(
    String email,
    String password,
    String name
) {}
