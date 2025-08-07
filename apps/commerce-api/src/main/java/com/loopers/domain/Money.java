package com.loopers.domain;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@NoArgsConstructor
public class Money {
    private BigDecimal value;

    public Money(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.INVALID_INPUT);
        }
        this.value = value;
    }

    public Money plus(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.INVALID_INPUT);
        }
        return new Money(this.value.add(value));
    }

    public Money minus(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.INVALID_INPUT);
        }
        return new Money(this.value.subtract(value));
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new CoreException(ErrorType.INVALID_INPUT);
        }
        return new Money(this.value.multiply(BigDecimal.valueOf(factor)));
    }

    public BigDecimal value() {
        return value;
    }

    public Long longValue() {
        return value.setScale(0, RoundingMode.DOWN).longValue();
    }
}
