package com.trufanov.service.impl;

import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.entity.Account;
import com.trufanov.entity.Customer;
import com.trufanov.entity.Transaction;
import com.trufanov.exception.EntityNotFoundException;
import com.trufanov.repository.AccountRepository;
import com.trufanov.repository.CustomerRepository;
import com.trufanov.repository.TransactionRepository;
import com.trufanov.service.AccountService;
import com.trufanov.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;

    @Override
    @Transactional
    public Long createAccount(CreateAccountRequestDto request) {
        //todo:
        //      - add rest exception handling with custom exceptions
        int initialBalanceComparison = request.initialCredit().compareTo(BigDecimal.ZERO);
        if (initialBalanceComparison < 0) {
            throw new RuntimeException("User can not be created with negative balance");
        }

        Customer customer = customerService.getCustomerInfo(request.customerId());

        Account account = new Account();
        account.setCustomer(customer);
        account = accountRepository.save(account);

        if (initialBalanceComparison > 0) {
            //todo: move to a separate transaction service
            Transaction transaction = new Transaction();
            transaction.setAmount(request.initialCredit());
            transaction.setAccount(account);
            transactionRepository.save(transaction);
        }

        return account.getId();
    }
}
