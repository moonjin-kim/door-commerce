package com.loopers.domain.stock;

import com.loopers.infrastructure.stock.StockJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class StockConcurrencyServiceTest {
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
    class Decrease {
        @DisplayName("재고 차감이 동시에 이루어져도 정상적으로 처리된다.")
        @Test
        void decreaseStockSuccessfully_whenPointIsUsedSimultaneously() throws InterruptedException {
            //given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            Stock stock1 = stockJpaRepository.save(new Stock(1L, 10));

            //when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        stockService.consume(StockCommand.Consume.of(1L, 1));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            //then
            Stock foundStock1 = stockJpaRepository.findById(stock1.getProductId()).get();
            assertAll(
                    () -> assertThat(foundStock1.getQuantity()).isEqualTo(stock1.getQuantity() - 10)
            );
        }
    }
}
