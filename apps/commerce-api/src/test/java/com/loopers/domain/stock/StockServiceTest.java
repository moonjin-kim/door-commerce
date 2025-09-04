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

import java.util.List;

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
                stockService.consume(new StockCommand.Consume(productId, quantity));
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
                stockService.consume(new StockCommand.Consume(productId, 11));
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
                stockService.consume(new StockCommand.Consume(productId, -1));
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
            stockService.consume(new StockCommand.Consume(productId, 10));

            //then
            Stock foundStock = stockJpaRepository.findById(productId).get();
            assertAll(
                    () -> assertThat(foundStock.getQuantity()).isEqualTo(stock.getQuantity() - 10)
            );
        }
    }

    @DisplayName("재고 롤백 처리 시")
    @Nested
    class Rollback {
        @DisplayName("존재하지 않는 상품 아이디가 주어지면 예외가 발생한다.")
        @Test
        void throwException_whenProductIdIsNotFound() {
            // given
            Long productId = 1L;
            int quantity = 10;

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                stockService.rollback(new StockCommand.Rollback(productId, quantity));
            });

            // then
            assertEquals(ErrorType.BAD_REQUEST, exception.getErrorType());
        }

        @DisplayName("재고가 정상적으로 증가한다.")
        @Test
        void rollbackStockSuccessfully() {
            // given
            Long productId = 1L;
            int initialQuantity = 10;
            Stock stock = stockJpaRepository.save(new Stock(productId, initialQuantity));

            // when
            stockService.rollback(new StockCommand.Rollback(productId, 5));

            // then
            Stock foundStock = stockJpaRepository.findById(productId).get();
            assertThat(foundStock.getQuantity()).isEqualTo(initialQuantity + 5);
        }
    }

    @DisplayName("여러 상품의 재고를 감소시킬 때,")
    @Nested
    class decreaseAll {
        @DisplayName("존재하지 않는 상품 아이디가 포함되면 NOT_FOUND 예외가 발생한다.")
        @Test
        void throwException_whenProductIdIsNotFound() {
            //given
            Long productId = 1L;
            Stock stock = stockJpaRepository.save(new Stock(productId, 1));

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                stockService.decreaseAll(
                        List.of(
                                new StockCommand.Rollback(productId, 1),
                                new StockCommand.Rollback(2L, 1)
                        )
                );
            });

            //then
            assertEquals(ErrorType.NOT_FOUND, exception.getErrorType());
        }

        @DisplayName("재고가 차감시킬 수량보다 적으면 INSUFFICIENT_STOCK 예외가 발생한다.")
        @Test
        void throwInsufficientStock_whenQuantityIsLack() {
            //given
            Stock stock1 = stockJpaRepository.save(new Stock(1L, 10));
            Stock stock2 = stockJpaRepository.save(new Stock(2L, 5));

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                stockService.decreaseAll(
                        List.of(
                                new StockCommand.Rollback(1L, 10),
                                new StockCommand.Rollback(2L, 6)
                        )
                );
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INSUFFICIENT_STOCK);
        }

        @DisplayName("차감 수량이 0보다 작은 요청이 있으면 BadRequest 예외가 발생한다.")
        @Test
        void throwInsufficientStock_whenQuantityIsLowZero() {
            //given
            Stock stock1 = stockJpaRepository.save(new Stock(1L, 10));
            Stock stock2 = stockJpaRepository.save(new Stock(2L, 5));

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                stockService.decreaseAll(
                        List.of(
                                new StockCommand.Rollback(1L, 10),
                                new StockCommand.Rollback(2L, -1)
                        )
                );
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("재고 감소가 성공적으로 이루어진다.")
        @Test
        void decreaseStockSuccessfully() {
            //given
            Stock stock1 = stockJpaRepository.save(new Stock(1L, 10));
            Stock stock2 = stockJpaRepository.save(new Stock(2L, 15));

            //when
            stockService.decreaseAll(
                    List.of(
                            new StockCommand.Rollback(1L, 10),
                            new StockCommand.Rollback(2L, 5)
                    )
            );

            //then
            Stock foundStock1 = stockJpaRepository.findById(stock1.getProductId()).get();
            Stock foundStock2 = stockJpaRepository.findById(stock2.getProductId()).get();
            assertAll(
                    () -> assertThat(foundStock1.getQuantity()).isEqualTo(stock1.getQuantity() - 10),
                    () -> assertThat(foundStock2.getQuantity()).isEqualTo(stock2.getQuantity() - 5)
            );
        }
    }

}
