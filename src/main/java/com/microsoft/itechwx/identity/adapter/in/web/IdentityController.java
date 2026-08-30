package com.microsoft.itechwx.identity.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.itechwx.identity.adapter.in.web.request.RegisterShopRequest;
import com.microsoft.itechwx.identity.adapter.in.web.request.LoginShopRequest;
import com.microsoft.itechwx.identity.adapter.in.web.response.RegisterShopResponse;
import com.microsoft.itechwx.identity.adapter.in.web.response.LoginShopResponse;
import com.microsoft.itechwx.identity.application.contract.command.LoginShopCommand;
import com.microsoft.itechwx.identity.application.contract.command.RegisterShopCommand;
import com.microsoft.itechwx.identity.application.contract.result.LoginShopResult;
import com.microsoft.itechwx.identity.application.contract.result.RegisterShopResult;
import com.microsoft.itechwx.identity.application.port.in.AuthenticationShopUseCase;
import com.microsoft.itechwx.identity.application.port.in.RegisterShopUseCase;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {
    private final RegisterShopUseCase registerShopUseCase;
    private final AuthenticationShopUseCase authenticationShopUseCase;

    public IdentityController(
        RegisterShopUseCase registerShopUseCase,
        AuthenticationShopUseCase authenticationShopUseCase
    ) {
        this.registerShopUseCase = registerShopUseCase;
        this.authenticationShopUseCase = authenticationShopUseCase;
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

        RegisterShopResponse response = new RegisterShopResponse(
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

    @PostMapping(
        path = "/login/shops",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginShopResponse> loginShop(
        @Valid
        @RequestBody LoginShopRequest request
    ) {
        LoginShopResult result = authenticationShopUseCase.loginShop(
            new LoginShopCommand(
                request.email(),
                request.password(),
                request.deviceName()
            )
        );

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noStore())
            .header(HttpHeaders.PRAGMA, "no-cache")
            .body(new LoginShopResponse(
                result.accountId(),
                result.accessToken(),
                result.refreshToken()
            ));
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
