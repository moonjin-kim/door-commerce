package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @DisplayName("재고를 생성할 때")
    @Nested
    class Create{

        @DisplayName("상품 ID/수량이 유효하면 재고를 생성한다")
        @Test
        void returnStock_whenValidDataIsProvide() {
            //given
            Long productId = 1L;
            int quantity = 10;
            StockCommand.Create command = StockCommand.Create.of(productId, quantity);

            //when
            Stock stock = Stock.init(command);

            //then
            assertAll(
                () -> assertThat(stock.getProductId()).isEqualTo(productId),
                () -> assertThat(stock.getQuantity()).isEqualTo(quantity)
            );
        }

        @DisplayName("상품 ID가 NULL이면 BadRequestException을 발생시킨다")
        @Test
        void throwBadRequest_whenProductIdIsNull() {
            //given
            Long productId = null;
            int quantity = 10;
            StockCommand.Create command = StockCommand.Create.of(productId, quantity);

            //when
            //then
            assertThrows(CoreException.class, () -> {
                Stock.init(command);
            });
        }

        @DisplayName("상품 개수가 음수이면 BadRequestException을 발생시킨다")
        @Test
        void throwBadRequest_whenQtity() {
            //given
            Long productId = 1L;
            int quantity = -1;
            StockCommand.Create command = StockCommand.Create.of(productId, quantity);

            //when
            //then
            assertThrows(CoreException.class, () -> {
                Stock.init(command);
            });
        }
    }

    @DisplayName("재고를 감소 요청할 때")
    @Nested
    class decrease{

        @DisplayName("감소 수량이 유효하면, 재고 수량이 감소한다")
        @Test
        void decreaseQuantity_whenValidDataIsProvide() {
            //given
            Long productId = 1L;
            int quantity = 10;
            StockCommand.Create command = StockCommand.Create.of(productId, quantity);
            Stock stock = Stock.init(command);

            //when
            stock.decrease(5);

            //then
            assertAll(
                    () -> assertThat(stock.getProductId()).isEqualTo(productId),
                    () -> assertThat(stock.getQuantity()).isEqualTo(5)
            );
        }

        @DisplayName("상품 감소 후 재고가 음수가 되면 CoreException을 발생시킨다")
        @Test
        void throwBadRequest_whenQuantity() {
            //given
            Long productId = 1L;
            int quantity = 5;
            StockCommand.Create command = StockCommand.Create.of(productId, quantity);
            Stock stock = Stock.init(command);

            //when
            //then
            assertThrows(CoreException.class, () -> {
                stock.decrease(6);
            });
        }
    }
}
