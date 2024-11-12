package com.trufanov.service.impl;

import com.trufanov.entity.Transaction;
import com.trufanov.repository.TransactionRepository;
import com.trufanov.service.AccountService;
import com.trufanov.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Override
    @Transactional
    public Long createAccountTransaction(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Transaction amount cannot be zero.");
        }
        //todo: fix circular dependency here
        //todo: add validation if account exists

        BigDecimal currentBalance = accountService.getAccountBalance(accountId);

        if (amount.compareTo(BigDecimal.ZERO) < 0 && currentBalance.add(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds: transaction would result in negative balance.");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setAccountId(accountId);
        transactionRepository.save(transaction);

        return transaction.getId();
    }

}
