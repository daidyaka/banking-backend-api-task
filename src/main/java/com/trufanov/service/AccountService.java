package com.trufanov.service;

import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.entity.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account getAccountInfo(Long accountId);

    Long createAccount(CreateAccountRequestDto request);

    BigDecimal getAccountBalance(Long accountId);

    void refreshAccountBalance(Long accountId);
}
