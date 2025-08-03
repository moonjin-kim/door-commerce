package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Point extends BaseEntity {
    @Column(nullable = false)
    private Long userId;
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="balance"))
    })
    private Money balance;
    @Version
    private Long version;

    public static Point init(Long userId) {
        Point point = new Point();
        point.userId = userId;
        point.balance = new Money(0L);

        return point;
    }

    public void charge(long amount) {
        if(amount <= 0) {
            throw new CoreException(ErrorType.INVALID_POINT_AMOUNT, "충전할 포인트는 0원 이상이어야 합니다.");
        }
        this.balance = this.balance.plus(amount);
    }

    public void use(long amount) {
        if(amount <= 0) {
            throw new CoreException(ErrorType.INVALID_POINT_AMOUNT, "사용할 포인트는 0원 이상이어야 합니다.");
        }
        this.balance = this.balance.minus(amount);
    }

    public Money balance() {
        return this.balance;
    }
}
