package com.microsoft.itechwx.identity.adapter.in.web;

import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.port.in.RegisterShopUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {
    private final RegisterShopUseCase registerShopUseCase;

    public IdentityController(RegisterShopUseCase registerShopUseCase) {
        this.registerShopUseCase = registerShopUseCase;
    }

    @RequestMapping("/register/shops")
    public ResponseEntity<> registerShop(){
        RegisterShopCommand command = new RegisterShopCommand();
        registerShopUseCase.register(command);
    }
}
