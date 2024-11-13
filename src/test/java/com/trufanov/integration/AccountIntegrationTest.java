package com.trufanov.integration;

import com.trufanov.AbstractApiTest;
import com.trufanov.dto.request.CreateAccountRequestDto;
import com.trufanov.entity.Account;
import com.trufanov.entity.Customer;
import com.trufanov.entity.Transaction;
import com.trufanov.repository.AccountRepository;
import com.trufanov.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountIntegrationTest extends AbstractApiTest {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void createAccount_ShouldReturnAccountId_WhenCustomerExists() throws Exception {
        Long customerId = createCustomer("John", "Doe");

        Long accountId = createAccount(customerId, BigDecimal.valueOf(500));
        assertNotNull(accountId);
    }

    @Test
    void createAccount_ShouldReturnError_WhenCustomerDoesNotExist() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto(999L, BigDecimal.valueOf(500));
        postRequest("/api/account/create", request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity with id 999 not found"));
    }

    @Test
    void createMultipleAccounts_AndVerifyBalances() throws Exception {
        Long customerId = createCustomer("John", "Doe");

        createAccount(customerId, BigDecimal.ZERO);
        createAccount(customerId, BigDecimal.valueOf(200));
        createAccount(customerId, BigDecimal.valueOf(300));

        BigDecimal expectedTotalBalance = BigDecimal.ZERO.add(BigDecimal.valueOf(200)).add(BigDecimal.valueOf(300));
        assertEquals(expectedTotalBalance, getRequest("/api/customer/" + customerId + "/info", Map.of(), Customer.class).getBalance());
    }

    @Test
    void createAccountWithInitialDeposit_AtomicCreationOfAccountAndTransaction() throws Exception {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
        Long customerId = createCustomer("Alice", "Johnson");

        BigDecimal initialDeposit = BigDecimal.valueOf(250.0);
        Long accountId = createAccount(customerId, initialDeposit);

        Account account = accountService.getAccountInfo(accountId);
        List<Transaction> transactions = transactionRepository.findAll().stream()
                .filter(transaction -> Objects.equals(transaction.getAccountId(), accountId))
                .toList();

        assertNotNull(account);
        assertEquals(customerId, account.getCustomerId());
        assertEquals(1, transactions.size());
        assertEquals("250.00", transactions.get(0).getAmount().toString());
        assertEquals(initialDeposit, accountService.getAccountBalance(accountId));
    }

    @Test
    void createAccount_ShouldRollbackTransactionOnFailure() throws Exception {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();

        Long customerId = createCustomer("Bob", "Smith");

        CreateAccountRequestDto invalidRequest = new CreateAccountRequestDto(customerId, BigDecimal.valueOf(-500));

        assertThrows(RuntimeException.class, () -> accountService.createAccount(invalidRequest));

        List<Account> accounts = accountRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAll();

        assertTrue(accounts.isEmpty(), "No accounts should have been created.");
        assertTrue(transactions.isEmpty(), "No transactions should have been created.");
    }


}
