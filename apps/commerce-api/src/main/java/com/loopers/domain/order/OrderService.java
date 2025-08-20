package com.loopers.domain.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Optional<Order> getByOrderId(String orderId) {
        // 주문 조회
        return orderRepository.findByOrderId(orderId);
    }


    @Transactional
    public Order complete(String orderId) {
        // 주문 조회
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문: " + orderId )
        );

        order.complete();

        return order;
    }

    @Transactional
    public Order cancel(String orderId) {
        // 주문 조회
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문: " + orderId )
        );

        order.cancel();

        return order;
    }

    @Transactional(readOnly = true)
    public PageResponse<Order> getOrders(
            PageRequest<OrderCommand.GetOrdersBy> command
    ) {
        // 주문 정보 반환
        return orderRepository.findAllBy(
                command.map(OrderCommand.GetOrdersBy::toParams)
        );
    }

    public List<Order> getPendingOrders() {
        // 주문 목록 조회
        return orderRepository.findAllBy(OrderStatus.PENDING);
    }
}
