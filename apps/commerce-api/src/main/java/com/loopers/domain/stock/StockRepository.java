package com.loopers.domain.stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository {
    Stock save(Stock stock);
    Optional<Stock> findBy(Long productId);
    Optional<Stock> findByIdWithPessimisticWriteLock(Long productId);
    List<Stock> findAllBy(List<Long> productIds);
    List<Stock> findAllByWithPessimisticWriteLock(List<Long> productIds);

}
