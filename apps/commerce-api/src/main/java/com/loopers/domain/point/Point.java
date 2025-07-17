package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.User;
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
    int amount;

    @Column(nullable = false)
    int balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PointStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Point charge(User user, int amount, int balance) {
        Point point = new Point();

        if(amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전할 포인트는 0원 이상이어야 합니다.");
        }
        
        point.amount = amount;
        point.balance = balance + amount;
        point.user = user;
        point.status = PointStatus.CHARGE;

        return point;
    }
}
