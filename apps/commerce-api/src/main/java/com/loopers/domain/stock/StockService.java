package com.loopers.domain.stock;

import com.loopers.domain.product.ProductInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    @Transactional
    public void decrease(StockCommand.Decrease command) {
        Stock stock = stockRepository.findBy(command.productId())
                .orElseThrow(() ->
                        new CoreException(ErrorType.BAD_REQUEST, "[productId = " + command.productId() + "] 존재하지 않는 재고입니다.")
                );

        stock.decrease(command.quantity());
    }

    @Transactional
    public void decreaseAll(List<StockCommand.Decrease> command) {
        List<Long> productIds = command.stream()
                .map(StockCommand.Decrease::productId)
                .toList();

        List<Stock> stocks = stockRepository.findAllBy(productIds);
        Map<Long, Stock> productMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getProductId, stock -> stock));

        command.forEach(commandItem -> {
            Stock stockItem = productMap.get(commandItem.productId());
            if (stockItem == null) {
                throw new CoreException(ErrorType.NOT_FOUND, "[productId = " + commandItem.productId() + "] 존재하지 않는 재고입니다.");
            }
            stockItem.decrease(commandItem.quantity());
        });
    }
}
