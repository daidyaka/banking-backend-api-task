package com.trufanov.service.impl;

import com.trufanov.entity.Transaction;
import com.trufanov.repository.TransactionRepository;
import com.trufanov.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Long createDepositTransaction(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Deposit transaction amount cannot be zero.");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setAccountId(accountId);
        transaction = transactionRepository.save(transaction);

        return transaction.getId();
    }

}
