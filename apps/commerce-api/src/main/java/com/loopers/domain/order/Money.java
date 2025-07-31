package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class Money {
    private Long value;

    public Money(Long value) {
        if (value < 0) {
            throw new CoreException(ErrorType.INVALID_INPUT);
        }
        this.value = value;
    }

    public Money plus(Long value) {
        return new Money(this.value + value);
    }

    public Money minus(Long value) {
        if (value < 0) {
            throw new CoreException(ErrorType.INVALID_INPUT);
        }
        return new Money(this.value - value);
    }

    public Long value() {
        return value;
    }
}
