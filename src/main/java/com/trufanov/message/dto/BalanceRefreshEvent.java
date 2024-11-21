package com.trufanov.message.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class BalanceRefreshEvent extends ApplicationEvent {

    @Getter
    private final Long accountId;

    public BalanceRefreshEvent(Object source, Long accountId) {
        super(source);
        this.accountId = accountId;
    }
}
