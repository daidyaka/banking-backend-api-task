package com.trufanov.service;

import com.trufanov.entity.Transaction;
import jakarta.transaction.Transactional;

public interface TransactionService {

    @Transactional
    void makeTransaction(Transaction transaction);
}
