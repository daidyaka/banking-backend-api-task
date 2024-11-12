package com.trufanov.service;

import java.math.BigDecimal;

public interface TransactionService {

    Long createAccountTransaction(Long accountId, BigDecimal amount);

}
