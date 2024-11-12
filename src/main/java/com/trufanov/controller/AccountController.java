package com.trufanov.controller;

import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.dto.response.CreateEntityResponseDto;
import com.trufanov.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public CreateEntityResponseDto createAccount(@RequestBody CreateAccountRequestDto request) {
        return new CreateEntityResponseDto(accountService.createAccount(request));
    }
}
