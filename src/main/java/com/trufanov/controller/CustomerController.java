package com.trufanov.controller;

import com.trufanov.dto.request.CreateCustomerRequestDto;
import com.trufanov.dto.response.CreateEntityResponseDto;
import com.trufanov.entity.Customer;
import com.trufanov.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public CreateEntityResponseDto createAccount(@RequestBody CreateCustomerRequestDto request) {
        return new CreateEntityResponseDto(customerService.createCustomer(request));
    }

    @GetMapping("/{customerId}/info")
    public Customer customerInfo(@PathVariable Long customerId) {
        return customerService.getCustomerInfo(customerId);
    }
}
