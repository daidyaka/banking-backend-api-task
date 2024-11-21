package com.trufanov.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.entity.Account;
import com.trufanov.entity.Transaction;
import com.trufanov.exception.EntityNotFoundException;
import com.trufanov.message.dto.TransactionEvent;
import com.trufanov.repository.AccountRepository;
import com.trufanov.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final ApplicationEventPublisher eventPublisher;

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
        account.setIncomingTransactions(Collections.emptyList());
        account.setOutgoingTransactions(Collections.emptyList());
        account = accountRepository.save(account);

        if (initialBalanceComparison > 0) {
            Transaction tr = new Transaction();
            tr.setToAccountId(account.getId());
            tr.setFromAccountId(account.getId());
            tr.setAmount(depositAmount);

            eventPublisher.publishEvent(new TransactionEvent(this, tr));
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

    @Override
    public void refreshBalanceCache(Long accountId) {
        accountBalanceCache.refresh(accountId);
    }

    private BigDecimal calculateAccountBalance(Long accountId) {
        Account account = getAccountInfo(accountId);
        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(account.getOutgoingTransactions());
        transactions.addAll(account.getIncomingTransactions());
        transactions.sort(Comparator.comparing(Transaction::getId));

        return transactions
                .stream()
                .map(transaction -> {
                    if (transaction.getFromAccountId().equals(accountId)) {
                        return transaction.getAmount().negate();
                    } else {
                        return transaction.getAmount();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
