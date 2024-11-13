package com.trufanov.integration;

import com.trufanov.AbstractApiTest;
import com.trufanov.entity.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerIntegrationTest extends AbstractApiTest {

    @Test
    void createCustomer_ShouldReturnCustomerId() throws Exception {
        Long customerId = createCustomer("Alice", "Johnson");

        assertNotNull(customerId);
    }

    @Test
    void getCustomerInfo_ShouldReturnCustomerDetails_WhenCustomerExists() throws Exception {
        Long customerId = createCustomer("Bob", "Brown");

        Customer customer = getRequest("/api/customer/" + customerId + "/info", new Object(), Customer.class);

        assertEquals("Bob", customer.getName());
        assertEquals("Brown", customer.getSurname());
        assertEquals(customerId, customer.getId());
    }

    @Test
    void getCustomerInfo_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        getRequest("/api/customer/999/info")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity with id 999 not found"));
    }

}
