package com.loopers.application.stock;

import com.loopers.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StockHandler {
    private final StockService stockService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void increaseStock(StockEvent.Increase event) {
        stockService.increase(event.toCommand());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void decreaseStock(StockEvent.Decrease event) {
        stockService.decrease(event.toCommand());
    }
}
