package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
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

    @Column(nullable = false)
    int balance;


    public static Point init(Long userId) {
        Point point = new Point();
        point.userId = userId;
        point.balance = 0;

        return point;
    }

    public void charge(int amount) {
        if(amount <= 0) {
            throw new CoreException(ErrorType.INVALID_POINT_AMOUNT, "충전할 포인트는 0원 이상이어야 합니다.");
        }
        this.balance += amount;
    }
}
