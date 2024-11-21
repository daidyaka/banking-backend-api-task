package com.trufanov.message;

import com.trufanov.message.dto.BalanceRefreshEvent;
import com.trufanov.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceRefreshEventListener {
    private final AccountService accountService;

    @EventListener
    public void handleEvent(BalanceRefreshEvent event) {
        accountService.refreshBalanceCache(event.getAccountId());
    }

}
