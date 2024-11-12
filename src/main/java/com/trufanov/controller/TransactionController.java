package com.trufanov.controller;

import com.trufanov.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/accounts")
public class TransactionController {

    private final AccountService accountService;



}
