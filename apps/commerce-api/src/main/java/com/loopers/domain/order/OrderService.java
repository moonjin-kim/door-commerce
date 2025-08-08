package com.loopers.domain.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order order(OrderCommand.Order order) {
        // 주문 저장
        return orderRepository.save(Order.create(order));
    }

    @Transactional(readOnly = true)
    public Optional<Order> getBy(OrderCommand.GetBy command) {
        // 주문 조회
        return orderRepository.findById(command.orderId());
    }

    @Transactional(readOnly = true)
    public PageResponse<Order> getOrders(
            PageRequest<OrderCommand.GetOrdersBy> command
    ) {
        // 주문 조회
        PageResponse<Order> orders = orderRepository.findAllBy(
                command.map(OrderCommand.GetOrdersBy::toParams)
        );

        // 주문 정보 반환
        return orders;
    }
}
