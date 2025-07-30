package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
    private Long pointUsed;
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
        if(userId == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("주문 정보가 올바르지 않습니다.");
        }
        this.userId = userId;
        this.orderItems = items;
        this.status = status;
        this.totalPrice = calculateTotalPrice();
        if(this.totalPrice < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST,"총 가격은 0보다 작을 수 없습니다.");
        }

        // todo: 지금은 포인트로 전액을 사용하는 구조이지만 결제가 생기면 분리할 예정
        this.pointUsed = this.totalPrice;
        if(this.pointUsed > this.totalPrice) {
            throw new CoreException(ErrorType.BAD_REQUEST,"사용된 포인트는 총 가격보다 클 수 없습니다.");
        }
    }

    public static Order order(OrderCommand.Order command) {
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
