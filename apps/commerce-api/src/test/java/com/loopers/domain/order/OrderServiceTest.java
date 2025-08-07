package com.loopers.domain.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.order.OrderInfo.OrderDto;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.loopers.domain.order.OrderCommand.GetOrdersBy.of;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

//    @AfterEach
//    void tearDown() {
//        databaseCleanUp.truncateAllTables();
//    }

    @DisplayName("주문을 생성할 때")
    @Nested
    class Order {
        @DisplayName("올바른 주문정보를 주면, 주문이 성공적으로 생성되어야 한다.")
        @Test
        void returnOrderInfo_whenCorrectCreateCommand() {
            // given
            OrderCommand.Order orderCommand = new OrderCommand.Order(
                    1L,
                    of(new OrderCommand.OrderItem(1L, "Product A", 1000L, 2))
            );

            // when
            OrderDto orderDto = orderService.order(orderCommand);

            // then
            assertAll(
                    () -> assertThat(orderDto.orderId()).isNotNull(),
                    () -> assertThat(orderDto.userId()).isEqualTo(1L),
                    () -> assertThat(orderDto.totalPrice()).isEqualTo(2000L),
                    () -> assertThat(orderDto.orderItemDtos()).hasSize(1),
                    () -> assertThat(orderDto.orderItemDtos().get(0).productId()).isEqualTo(1L),
                    () -> assertThat(orderDto.orderItemDtos().get(0).name()).isEqualTo("Product A"),
                    () -> assertThat(orderDto.orderItemDtos().get(0).price()).isEqualTo(1000L),
                    () -> assertThat(orderDto.orderItemDtos().get(0).quantity()).isEqualTo(2)
            );
        }

        @DisplayName("유저 ID가 null인 경우 BadRequest 예외를 발생시켜야 한다.")
        @Test
        void throwBadRequest_whenUserIdIsNull() {
            // given
            OrderCommand.Order orderCommand = new OrderCommand.Order(
                    null,
                    of(new OrderCommand.OrderItem(1L, "Product A", 1000L, 2))
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                orderService.order(orderCommand);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("유저 ID가 null인 경우 INVALID_INPUT 예외를 발생시켜야 한다.")
        @Test
        void throwBadRequest_whenInvalidCreateItem() {
            // given
            OrderCommand.Order orderCommand = new OrderCommand.Order(
                    1L,
                    of(new OrderCommand.OrderItem(null, "Product A", 1000L, 2))
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                orderService.order(orderCommand);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }

    @DisplayName("주문 정보를 조회할 때,")
    @Nested
    class GetBy {
        @DisplayName("존재하는 주문 ID와 유저 ID를 주면, 주문 정보를 반환해야 한다.")
        @Test
        void returnOrderInfo_whenValidOrderIdAndUserId() {
            // given
            OrderCommand.Order orderCommand = new OrderCommand.Order(
                    2L,
                    of(new OrderCommand.OrderItem(1L, "Product A", 1000L, 2))
            );
            OrderDto createdOrder1 = orderService.order(orderCommand);

            // when
            OrderDto orderDto = orderService.getBy(new OrderCommand.GetBy(createdOrder1.orderId(), 2L));

            // then
            assertThat(orderDto).isEqualTo(createdOrder1);
        }

        @DisplayName("존재하지 않는 주문 ID를 주면, NOT_FOUND 예외를 발생시켜야 한다.")
        @Test
        void throwNotFound_whenInvalidOrderId() {
            // given
            Long invalidOrderId = 999L;

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                orderService.getBy(new OrderCommand.GetBy(invalidOrderId, 1L));
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("주문자가 아닌 유저 ID를 주면, FORBIDDEN 예외를 발생시켜야 한다.")
        @Test
        void throwForbidden_whenUnauthorizedUser() {
            // given
            OrderCommand.Order orderCommand = new OrderCommand.Order(
                    1L,
                    of(new OrderCommand.OrderItem(1L, "Product A", 1000L, 2))
            );
            OrderDto createdOrder = orderService.order(orderCommand);

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                orderService.getBy(new OrderCommand.GetBy(createdOrder.orderId(), 2L));
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.FORBIDDEN);
        }
    }

    @DisplayName("주문 목록을 요청할때")
    @Nested
    class getOrders {
        @DisplayName("사용자 Id가 주어지면, 사용자의 주문 정보 목록을 반환해야 한다.")
        @Test
        void returnCreateList_whenGetOrders() {
            // given
            OrderCommand.Order orderCommand1 = new OrderCommand.Order(
                    1L,
                    of(new OrderCommand.OrderItem(1L, "Product A", 1000L, 2))
            );
            OrderCommand.Order orderCommand2 = new OrderCommand.Order(
                    1L,
                    of(new OrderCommand.OrderItem(2L, "Product B", 2000L, 1))
            );
            orderService.order(orderCommand1);
            orderService.order(orderCommand2);
            PageRequest<OrderCommand.GetOrdersBy> command = new PageRequest<>(1, 10, OrderCommand.GetOrdersBy.of(1L));

            // when
            PageResponse<OrderDto> orders = orderService.getOrders(
                    command
            );

            // then
            assertThat(orders.getItems()).hasSize(2);
        }
    }
}
