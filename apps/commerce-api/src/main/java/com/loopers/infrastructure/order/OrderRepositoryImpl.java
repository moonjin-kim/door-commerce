package com.loopers.infrastructure.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public PageResponse<Order> findAllBy(PageRequest<OrderParams.GetOrdersBy> params) {
        return orderQueryDslRepository.findAllBy(params);
    }
}
