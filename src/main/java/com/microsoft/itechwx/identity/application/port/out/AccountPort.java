package com.microsoft.itechwx.identity.application.port.out;

import com.microsoft.itechwx.identity.domain.AccountAuthenticationModel;
import com.microsoft.itechwx.identity.domain.AccountModel;

public interface AccountPort {
    
    AccountModel save(
        AccountModel account
    );

}
