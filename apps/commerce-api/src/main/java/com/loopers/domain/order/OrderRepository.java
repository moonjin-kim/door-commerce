package com.loopers.domain.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.order.OrderParams;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    PageResponse<Order> findAllBy(PageRequest<OrderParams.GetOrdersBy> pageRequest);
}
