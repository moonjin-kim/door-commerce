package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public void decrease(Long productId, int quantity) {
        Stock stock = stockRepository.findBy(productId)
                .orElseThrow(() ->
                        new CoreException(ErrorType.BAD_REQUEST, "[productId = " + productId + "] 존재하지 않는 재고입니다.")
                );

        stock.decrease(quantity);
    }
}
