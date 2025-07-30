package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // order가 SQL 예약어인 경우가 많아 orders로 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Column
    private Long userId;
    @Column
    private Long totalPrice;
    @Column
    LocalDateTime orderDate;
    @Column
    private OrderStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "orders_item_list",
            joinColumns = @JoinColumn(name = "order_number")
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(Long userId, List<OrderItem> items, OrderStatus status) {
        this.userId = userId;
        this.orderItems = items;
        this.status = status;
        this.totalPrice = calculateTotalPrice();
    }

    public static Order createOrder(OrderCommand.Order command) {
        List<OrderItem> orderItems = command.orderItems().stream()
                .map(OrderItem::create)
                .toList();


        return new Order(command.userId(), orderItems,OrderStatus.PENDING);
    }

    private Long calculateTotalPrice() {
        return this.totalPrice = orderItems.stream()
                .mapToLong(OrderItem::getTotalAmount)
                .sum();
    }
}
