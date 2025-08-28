package com.loopers.interfaces.event.stock;

import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StockListener {
    private final StockService stockService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handle(OrderEvent.ConsumeStockCommand event) {
        stockService.consume(StockCommand.Consume.from(event));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handle(OrderEvent.RollbackStockCommand event) {
        stockService.rollback(StockCommand.Rollback.from(event));
    }

}
