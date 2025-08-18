package com.loopers.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
public class Money {
    private BigDecimal value;

    protected Money() {}

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

    @JsonValue
    public BigDecimal value() {
        return value;
    }

    public Long longValue() {
        return value.setScale(0, RoundingMode.DOWN).longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Money price = (Money) o;
        return Objects.equals(value, price.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
