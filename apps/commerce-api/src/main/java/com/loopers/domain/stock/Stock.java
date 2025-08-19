package com.loopers.domain.stock;


import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stock extends BaseEntity {
    @Column(unique = true)
    private Long productId;
    @Column(unique = false)
    private int quantity;

    protected Stock(Long productId, int quantity) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product id cannot be null");
        }

        if (quantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고는 음수가 될 수 없습니다.");
        }

        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock create(StockCommand.Create command) {
        return new Stock(command.productId(), command.quantity());
    }

    public void decrease(int quantity) {
        if(quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고 감소 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < quantity) {
            throw new CoreException(ErrorType.INSUFFICIENT_STOCK);
        }
        this.quantity -= quantity;
    }

    public void increase(int quantity) {
        if(quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고 증가 수량은 0보다 커야 합니다.");
        }
        this.quantity += quantity;
    }
}
