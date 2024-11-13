package com.trufanov.controller;

import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.dto.response.CreateEntityResponseDto;
import com.trufanov.service.AccountService;
import com.trufanov.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final CustomerService customerService;
    private final AccountService accountService;

    @PostMapping("/create")
    public CreateEntityResponseDto createAccount(@RequestBody CreateAccountRequestDto request) {
        //it will fail if customer doesn't exist
        customerService.getCustomerInfo(request.customerId());
        return new CreateEntityResponseDto(accountService.createAccount(request));
    }
}
