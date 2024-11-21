package com.trufanov.message;

import com.trufanov.message.dto.BalanceRefreshEvent;
import com.trufanov.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BalanceRefreshEventListener {
    private final AccountService accountService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(BalanceRefreshEvent event) {
        accountService.refreshBalanceCache(event.getAccountId());
    }

}
