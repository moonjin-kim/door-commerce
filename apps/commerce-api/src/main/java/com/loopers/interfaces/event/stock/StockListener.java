package com.loopers.interfaces.event.stock;

import com.loopers.application.stock.StockFacade;
import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StockListener {
    private final StockFacade stockService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handle(OrderEvent.ConsumeStockCommand event) {
        stockService.consumeStock(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handle(OrderEvent.RollbackStockCommand event) {
        stockService.rollbackStock(event);
    }

}
