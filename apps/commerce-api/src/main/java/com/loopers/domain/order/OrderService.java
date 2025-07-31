package com.loopers.domain.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public OrderInfo.OrderDto order(OrderCommand.Order order) {
        // 주문 저장
        Order savedOrder = orderRepository.save(Order.order(order));

        // 주문 정보 반환
        return OrderInfo.OrderDto.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderInfo.OrderDto getBy(OrderCommand.GetBy command) {
        // 주문 조회
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "[id = " + command.orderId() + "] 존재하지 않는 주문입니다."));

        // 주문자가 요청한 주문인지 확인
        order.checkPermission(command.userId());

        // 주문 정보 반환
        return OrderInfo.OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderInfo.OrderDto> getOrders(
            PageRequest<OrderCommand.GetOrdersBy> command
    ) {
        // 주문 조회
        PageResponse<Order> orders = orderRepository.findAllBy(
                command.map(OrderCommand.GetOrdersBy::toParams)
        );

        // 주문 정보 반환
        return orders.map(OrderInfo.OrderDto::from);
    }
}
