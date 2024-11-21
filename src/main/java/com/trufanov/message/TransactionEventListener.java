package com.trufanov.message;

import com.trufanov.message.dto.TransactionEvent;
import com.trufanov.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final TransactionService transactionService;

    @EventListener
    public void handleTransactionEvent(TransactionEvent transactionEvent) {
        transactionService.makeTransaction(transactionEvent.getTransaction());
    }
}
