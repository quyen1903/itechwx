package com.microsoft.itechwx.identity.application.port.in;

import com.microsoft.itechwx.identity.application.contract.command.CreateAccountCommand;
import com.microsoft.itechwx.identity.application.contract.result.CreatedAccount;

public interface CreateAccountUseCase {
    CreatedAccount register(CreateAccountCommand command);
}
