package com.microsoft.itechwx.onboarding.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.itechwx.onboarding.application.port.in.RegisterShopUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/identity")
public final class ShopRegistrationController {

    private final RegisterShopUseCase registerShopUseCase;

    public ShopRegistrationController(RegisterShopUseCase registerShopUseCase) {
        this.registerShopUseCase = registerShopUseCase;
    }

    @PostMapping("/register/shops")
    public ResponseEntity<RegisterShopResponse> registerShop(
        @Valid @RequestBody RegisterShopRequest request
    ) {
        registerShopUseCase.register(request.toCommand());
        return ResponseEntity.accepted().body(RegisterShopResponse.received());
    }
}
