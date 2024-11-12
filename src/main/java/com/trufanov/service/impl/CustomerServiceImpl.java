package com.trufanov.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.trufanov.dto.request.CreateCustomerRequestDto;
import com.trufanov.entity.Customer;
import com.trufanov.entity.Transaction;
import com.trufanov.exception.EntityNotFoundException;
import com.trufanov.repository.CustomerRepository;
import com.trufanov.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final LoadingCache<Long, BigDecimal> customerBalanceCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public BigDecimal load(Long customerId) {
                    return calculateCustomerBalance(customerId);
                }
            });


    @Override
    @Transactional
    public Long createCustomer(CreateCustomerRequestDto request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setSurname(request.surname());
        customer.setAccounts(Collections.emptyList());

        customer = customerRepository.save(customer);

        return customer.getId();
    }

    @Override
    public Customer getCustomerInfo(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(customerId));
    }

    @Override
    public BigDecimal getCustomerTotalBalance(Long customerId) {
        try {
            return customerBalanceCache.get(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Customer not found or balance calculation failed", e);
        }
    }

    public void refreshCustomerBalance(Long customerId) {
        customerBalanceCache.refresh(customerId);
    }

    private BigDecimal calculateCustomerBalance(Long customerId) {
        return getCustomerInfo(customerId)
                .getAccounts()
                .stream()
                .flatMap(account -> account.getTransactions().stream())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
