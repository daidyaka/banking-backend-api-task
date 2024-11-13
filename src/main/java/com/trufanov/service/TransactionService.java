package com.trufanov.service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;

public interface TransactionService {

    @Transactional
    Long createDepositTransaction(Long accountId, BigDecimal amount);

}
