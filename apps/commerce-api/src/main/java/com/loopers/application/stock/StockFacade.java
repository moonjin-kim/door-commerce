package com.loopers.application.stock;

import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StockFacade {
    private final StockService stockService;

    public void consumeStock(OrderEvent.ConsumeStockCommand event) {
        stockService.consume(StockCommand.Consume.from(event));
    }

    public void rollbackStock(OrderEvent.RollbackStockCommand event) {
        stockService.rollback(StockCommand.Rollback.from(event));
    }
}
