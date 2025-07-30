package com.loopers.domain.stock;

import com.loopers.infrastructure.stock.StockJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품의 재고를 감소시킬 때,")
    @Nested
    class decrease {
        @DisplayName("존재하지 않는 상품 아이디가 주어지면 예외가 발생한다.")
        @Test
        void throwException_whenProductIdIsNotFound() {
            //given
            Long productId = 1L;
            int quantity = 10;

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                stockService.decrease(new StockCommand.Decrease(productId, quantity));
            });

            //then
            assertEquals(ErrorType.BAD_REQUEST, exception.getErrorType());
        }

        @DisplayName("재고가 차감시킬 수량보다 적으면 INSUFFICIENT_STOCK 예외가 발생한다.")
        @Test
        void throwInsufficientStock_whenQuantityIsLack() {
            //given
            Long productId = 1L;
            int initialQuantity = 10;
            Stock stock = stockJpaRepository.save(new Stock(productId, initialQuantity));

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                stockService.decrease(new StockCommand.Decrease(productId, 11));
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INSUFFICIENT_STOCK);
        }

        @DisplayName("차감 수량이 0보다 작으면 BadRequest 예외가 발생한다.")
        @Test
        void throwInsufficientStock_whenQuantityIsLowZero() {
            //given
            Long productId = 1L;
            int initialQuantity = 10;
            Stock stock = stockJpaRepository.save(new Stock(productId, initialQuantity));

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                stockService.decrease(new StockCommand.Decrease(productId, -1));
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("재고 감소가 성공적으로 이루어진다.")
        @Test
        void decreaseStockSuccessfully() {
            //given
            Long productId = 1L;
            int initialQuantity = 100;
            Stock stock = stockJpaRepository.save(new Stock(productId, initialQuantity));

            //when
            stockService.decrease(new StockCommand.Decrease(productId, 10));

            //then
            Stock foundStock = stockJpaRepository.findById(productId).get();
            assertAll(
                    () -> assertThat(foundStock.getQuantity()).isEqualTo(stock.getQuantity() - 10)
            );
        }
    }

}
