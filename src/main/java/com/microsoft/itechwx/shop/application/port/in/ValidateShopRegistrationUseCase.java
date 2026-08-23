package com.microsoft.itechwx.shop.application.port.in;

import com.microsoft.itechwx.shop.application.contract.command.CreateShopCommand;

public interface ValidateShopRegistrationUseCase {

    void validate(CreateShopCommand command);
}
