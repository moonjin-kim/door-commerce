package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @DisplayName("주문을 생성할 때")
    @Nested
    class OrderCreate {
        @DisplayName("상품의 가격에 음수가 포함되면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void returnOrder_whenProductPriceIsNegative(){
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", -2000L, 5)
                    )
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                Order.order(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("유저ID가 null이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwBadRequest_whenProductPriceIsNegative(){
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    null,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", -2000L, 5)
                    )
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                Order.order(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("주문 수량이 0이하이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwBadRequest_whenQuantityIsZero(){
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 0),
                            OrderCommand.OrderItem.of(2L, "상품2", -2000L, 5)
                    )
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                Order.order(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("주문 정보가 올바르게 맞으면, 주문이 생성된다.")
        @Test
        void returnOrder_whenValidData(){
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 2000L, 5)
                    )
            );

            //when
            Order order = Order.order(command);

            //then
            assertAll(
                    ()-> assertEquals(1L, order.getUserId()),
                    () -> assertEquals(11000L, order.getTotalAmount().value()),
                    () -> assertEquals(11000L, order.getPointUsed().value()),
                    () -> assertEquals(OrderStatus.CONFIRMED, order.getStatus()),
                    () -> assertEquals(2, order.getOrderItems().size()),
                    () -> assertEquals("상품1", order.getOrderItems().get(0).getName()),
                    () -> assertEquals("상품2", order.getOrderItems().get(1).getName())
            );
        }
    }

    @DisplayName("주문이 유저의 권한인지 확인할 때")
    @Nested
    class CheckPermission {
        @DisplayName("주문한 유저이면, 아무일도 발생하지 않는다.")
        @Test
        void returnOrder_whenProductPriceIsNegative(){
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = Order.order(command);

            //when
            order.checkPermission(1L);

            // then
        }

        @DisplayName("유저ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequest_whenProductPriceIsNegative(){
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = Order.order(command);

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                order.checkPermission(null);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("주문을 요청한 유저가 아니면, FORBIDDEN 예외가 발생한다.")
        @Test
        void throwForbidden_whenUserIsNotOrder(){
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = Order.order(command);

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                order.checkPermission(2L);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.FORBIDDEN);
        }
    }

}
