package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderInfo.OrderDto order(OrderCommand.Order order) {
        // 주문 저장
        Order savedOrder = orderRepository.save(Order.createOrder(order));

        // 주문 정보 반환
        return OrderInfo.OrderDto.of(savedOrder);
    }
}
