package com.trufanov.service;

import com.trufanov.dto.request.CreateCustomerRequestDto;
import com.trufanov.entity.Customer;

public interface CustomerService {
    Long createCustomer(CreateCustomerRequestDto request);
    Customer getCustomerInfo(Long customerId);
}
