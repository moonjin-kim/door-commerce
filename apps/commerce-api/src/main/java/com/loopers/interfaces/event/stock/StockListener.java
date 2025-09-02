package com.loopers.interfaces.event.stock;

import com.loopers.application.stock.StockFacade;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockListener {
    private final StockFacade stockService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleConsumeStock(OrderEvent.ConsumeStockCommand event) {
        stockService.consumeStock(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRollbackStock(OrderEvent.RollbackStockCommand event) {
        stockService.rollbackStock(event);
    }
}
