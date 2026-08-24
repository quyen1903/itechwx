package com.microsoft.itechwx.identity.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.itechwx.identity.application.port.in.CreateAccountUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {
    private final CreateAccountUseCase createAccountUseCase;
    public IdentityController(
        CreateAccountUseCase createAccountUseCase
    ){
        this.createAccountUseCase = createAccountUseCase;
    }

    @RequestMapping("/register/shop")
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<void> registerShop(
        @RequestBody 
    ){

    }
}
