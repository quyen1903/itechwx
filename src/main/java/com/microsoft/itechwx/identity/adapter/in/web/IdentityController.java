package com.microsoft.itechwx.identity.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.itechwx.identity.adapter.in.web.request.RegisterShopRequest;
import com.microsoft.itechwx.identity.adapter.in.web.response.RegisterShopResponse;
import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.RegisterShopResult;
import com.microsoft.itechwx.identity.application.port.in.RegisterShopUseCase;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {
    private final RegisterShopUseCase registerShopUseCase;

    public IdentityController(RegisterShopUseCase registerShopUseCase) {
        this.registerShopUseCase = registerShopUseCase;
    }


    @PostMapping(
        path = "/register/shops",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RegisterShopResponse> registerShop(
        @Valid
        @RequestBody 
        RegisterShopRequest request 
    ){
        RegisterShopCommand command = toCommand(request);

        RegisterShopResult result = registerShopUseCase.registerShop(command);

        var response = new RegisterShopResponse(
            result.accountId(),
            result.accessToken(),
            result.refreshToken()
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .cacheControl(CacheControl.noStore())
            .header(HttpHeaders.PRAGMA, "no-cache")
            .body(response);
    }

    private static RegisterShopCommand toCommand (RegisterShopRequest request){
        return new RegisterShopCommand(
            request.name(),
            request.phone(),
            request.address(),
            request.timezone(),
            request.language(),
            request.email(),
            request.password(),
            request.username(),
            request.businessName(),
            request.businessType(),
            request.taxId(),
            request.currency()
        );
    }
}
