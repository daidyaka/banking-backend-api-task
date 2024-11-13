package com.trufanov;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.dto.request.CreateCustomerRequestDto;
import com.trufanov.dto.response.CreateEntityResponseDto;
import com.trufanov.service.AccountService;
import com.trufanov.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractApiTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected CustomerService customerService;


    protected ResultActions postRequest(String url, Object content) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    protected <T> T postRequest(String url, Object content, Class<T> clazz) throws Exception {
        String response = postRequest(url, content)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return parseResponse(response, clazz);
    }

    protected ResultActions getRequest(String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    protected <T> T getRequest(String url, Object content, Class<T> clazz) throws Exception {
        String response = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return parseResponse(response, clazz);
    }

    protected Long createCustomer(String name, String surname) throws Exception {
        CreateCustomerRequestDto request = new CreateCustomerRequestDto(name, surname);
        return postRequest("/api/customer/create", request, CreateEntityResponseDto.class).id();
    }


    protected Long createAccount(Long customerId, BigDecimal initialCredit) throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto(customerId, initialCredit);
        return postRequest("/api/account/create", request, CreateEntityResponseDto.class).id();
    }

    protected <T> T parseResponse(String response, Class<T> responseClass) throws Exception {
        return objectMapper.readValue(response, responseClass);
    }
}

