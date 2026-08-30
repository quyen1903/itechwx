package com.microsoft.itechwx.identity.application.port.out;

import com.microsoft.itechwx.identity.domain.Account;

public interface AccountPort {
    
    Account save(
        Account account
    );

}
