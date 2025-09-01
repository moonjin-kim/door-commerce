package com.loopers.interfaces.event.stock;

import com.loopers.application.stock.StockFacade;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockListener {
    private final StockFacade stockService;
    
    @KafkaListener(topics = "order.consume-stock", groupId = "stock-listener", containerFactory = KafkaConfig.BATCH_LISTENER)
    public void handleConsumeStock(List<OrderEvent.ConsumeStockCommand> events) {
        for (OrderEvent.ConsumeStockCommand event : events) {
            stockService.consumeStock(event);
        }
    }

    @KafkaListener(topics = "order.rollback-stock", groupId = "stock-listener", containerFactory = KafkaConfig.BATCH_LISTENER)
    public void handleRollbackStock(List<OrderEvent.RollbackStockCommand> events) {
        for (OrderEvent.RollbackStockCommand event : events) {
            stockService.rollbackStock(event);
        }
    }
}
