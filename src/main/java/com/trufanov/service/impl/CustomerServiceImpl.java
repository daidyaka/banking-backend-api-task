package com.trufanov.service.impl;

import com.trufanov.dto.request.CreateCustomerRequestDto;
import com.trufanov.entity.Customer;
import com.trufanov.exception.EntityNotFoundException;
import com.trufanov.repository.CustomerRepository;
import com.trufanov.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

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
}
