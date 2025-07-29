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
    Long productId;
    @Column
    int quantity;

    protected Stock(Long productId, int quantity) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product id cannot be null");
        }
        this.productId = productId;
        if (quantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고는 음수가 될 수 없습니다.");
        }
        this.quantity = quantity;
    }

    public static Stock init(StockCommand.Create command) {
        return new Stock(command.productId(), command.quantity());
    }

    public void decrease(int quantity) {
        if (this.quantity < quantity) {
            throw new CoreException(ErrorType.INSUFFICIENT_STOCK);
        }
        this.quantity -= quantity;
    }
}
