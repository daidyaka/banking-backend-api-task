package com.trufanov.message.dto;

import com.trufanov.entity.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionEvent extends ApplicationEvent {

    private final Transaction transaction;

    public TransactionEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
