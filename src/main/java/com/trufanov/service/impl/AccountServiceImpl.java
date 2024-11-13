package com.trufanov.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.entity.Account;
import com.trufanov.entity.Transaction;
import com.trufanov.exception.EntityNotFoundException;
import com.trufanov.repository.AccountRepository;
import com.trufanov.service.AccountService;
import com.trufanov.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final TransactionService transactionService;

    //cache to prevent possible DoS attacks
    private final LoadingCache<Long, BigDecimal> accountBalanceCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public BigDecimal load(Long accountId) {
                    return calculateAccountBalance(accountId);
                }
            });

    @Override
    public Account getAccountInfo(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(accountId));
    }

    @Override
    @Transactional
    public Long createAccount(CreateAccountRequestDto request) {
        BigDecimal depositAmount = request.initialCredit();
        int initialBalanceComparison = depositAmount.compareTo(BigDecimal.ZERO);
        if (initialBalanceComparison < 0) {
            throw new RuntimeException("User can not be created with negative balance");
        }

        Account account = new Account();
        account.setCustomerId(request.customerId());
        account.setTransactions(Collections.emptyList());
        account = accountRepository.save(account);

        if (initialBalanceComparison > 0) {
            transactionService.createDepositTransaction(account.getId(), depositAmount);
            accountBalanceCache.put(account.getId(), depositAmount);
        }

        return account.getId();
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        try {
            return accountBalanceCache.get(accountId);
        } catch (ExecutionException e) {
            BigDecimal balance = calculateAccountBalance(accountId);
            accountBalanceCache.put(accountId, balance);
            return balance;
        }
    }

    private BigDecimal calculateAccountBalance(Long accountId) {
        return getAccountInfo(accountId)
                .getTransactions()
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
