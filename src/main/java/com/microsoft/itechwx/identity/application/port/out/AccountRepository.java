package com.microsoft.itechwx.identity.application.port.out;

import com.microsoft.itechwx.identity.domain.Account;

public interface AccountRepository {
    Account save(Account account);
}
