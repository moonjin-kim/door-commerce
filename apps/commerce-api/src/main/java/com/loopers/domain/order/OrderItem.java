package com.loopers.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem {
    @Column()
    private Long productId;
    @Column()
    private String name;
    @Column()
    private long productPrice;
    @Column()
    private int quantity;

    private OrderItem(Long productId, String name, long productPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public static OrderItem create(OrderCommand.OrderItem command) {
        return new OrderItem(command.productId(), command.name(), command.price(), command.quantity());
    }

    public Long getTotalAmount() {
        return ((long) productPrice * quantity);
    }
}
