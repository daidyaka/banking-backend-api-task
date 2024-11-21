package com.trufanov.service.impl;

import com.trufanov.entity.Account;
import com.trufanov.entity.Transaction;
import com.trufanov.message.dto.BalanceRefreshEvent;
import com.trufanov.repository.TransactionRepository;
import com.trufanov.service.AccountService;
import com.trufanov.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void makeTransaction(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        Account fromAccount = accountService.getAccountInfo(transaction.getFromAccountId());
        Account toAccount = accountService.getAccountInfo(transaction.getToAccountId());

        BigDecimal fromAccountBalance = accountService.getAccountBalance(fromAccount.getId());
        if (!Objects.equals(transaction.getFromAccountId(), transaction.getToAccountId())
                && fromAccountBalance.compareTo(transaction.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds in source account.");
        }

        transactionRepository.save(transaction);

        eventPublisher.publishEvent(new BalanceRefreshEvent(this, fromAccount.getId()));
        eventPublisher.publishEvent(new BalanceRefreshEvent(this, toAccount.getId()));
    }
}
