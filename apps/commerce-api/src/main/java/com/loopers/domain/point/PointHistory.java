package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointHistory extends BaseEntity {
    private Long orderId;
    private int amount;
    private PointStatus status;

    public PointHistory(int amount, Long orderId, PointStatus status) {
        this.amount = amount;
        this.orderId = orderId;
        this.status = status;
    }

    static PointHistory charge(PointCommand.Charge command) {
        if(command.amount() <= 0) {
            throw new CoreException(ErrorType.INVALID_POINT_AMOUNT);
        }

        return new PointHistory(command.amount(),null, PointStatus.CHARGE);
    }

    static PointHistory use(PointCommand.Using command) {
        if(command.orderId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 ID가 없습니다.");
        }
        return new PointHistory(-command.amount(), command.orderId(), PointStatus.USE);
    }
}
