package com.loopers.infrastructure.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderQueryDslRepository orderQueryDslRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public Optional<Order> findByOrderId(String orderId) {
        return orderJpaRepository.findByOrderId(orderId);
    }

    @Override
    public PageResponse<Order> findAllBy(PageRequest<OrderParams.GetOrdersBy> params) {
        return orderQueryDslRepository.findAllBy(params);
    }

    @Override
    public List<Order> findAllBy(OrderStatus status) {
        return orderJpaRepository.findAllByStatus(status);
    }
}
