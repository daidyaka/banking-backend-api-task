package com.trufanov.controller;

import com.trufanov.dto.request.CreateCustomerRequestDto;
import com.trufanov.dto.response.CreateEntityResponseDto;
import com.trufanov.entity.Customer;
import com.trufanov.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public CreateEntityResponseDto createCustomer(@RequestBody CreateCustomerRequestDto request) {
        return new CreateEntityResponseDto(customerService.createCustomer(request));
    }

    @GetMapping("/{customerId}/info")
    public Customer customerInfo(@PathVariable Long customerId) {
        Customer customerInfo = customerService.getCustomerInfo(customerId);
        customerInfo.setBalance(customerService.getCustomerTotalBalance(customerId));
        return customerInfo;
    }
}
