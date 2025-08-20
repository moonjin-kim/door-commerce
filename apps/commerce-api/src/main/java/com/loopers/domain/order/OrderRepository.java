package com.loopers.domain.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.order.OrderParams;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderId(String orderId);
    PageResponse<Order> findAllBy(PageRequest<OrderParams.GetOrdersBy> pageRequest);
    List<Order> findAllBy(OrderStatus status);
}
