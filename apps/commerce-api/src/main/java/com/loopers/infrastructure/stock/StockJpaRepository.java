package com.loopers.infrastructure.stock;

import com.loopers.domain.stock.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductId(Long productId);
    List<Stock> findByProductIdIn(List<Long> productIds);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.productId = :productId")
    Optional<Stock> findByIdWithPessimisticWriteLock(@Param("productId") Long productId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.productId IN :productIds")
    List<Stock> findStocksByWithPessimisticWriteLock(@Param("productIds") List<Long> productIds);
}
