package com.loopers.domain.order;

import com.loopers.domain.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem {
    @Column()
    private Long productId;
    @Column()
    private String name;
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="product_price"))
    })
    private Money productPrice;
    @Column()
    private int quantity;

    private OrderItem(Long productId, String name, Long productPrice, int quantity) {
        if (productId == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "상품 ID는 null일 수 없습니다.");
        }
        this.productId = productId;

        if (name == null || name.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "상품 이름은 null이거나 비어있을 수 없습니다.");
        }
        this.name = name;

        if (quantity <= 0) {
            throw new CoreException(ErrorType.INVALID_INPUT, "수량은 0보다 커야 합니다.");
        }
        this.quantity = quantity;

        this.productPrice = new Money(new BigDecimal(productPrice));
    }

    public static OrderItem create(OrderCommand.OrderItem command) {
        return new OrderItem(command.productId(), command.name(), command.price(), command.quantity());
    }

    public BigDecimal getTotalAmount() {
        return productPrice.multiply(quantity).value();
    }
}
