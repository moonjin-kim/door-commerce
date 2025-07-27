package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class Amount {
    @Column(name = "price", nullable = false)
    private Long price;

    public Amount(Long price) {
        if (price == null || price < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 비어있을 수 없고, 0보다 커야 합니다.");
        }
        this.price = price;
    }

    public Long getPrice() {
        return price;
    }
}
