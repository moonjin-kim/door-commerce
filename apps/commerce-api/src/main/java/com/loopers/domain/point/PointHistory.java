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
    private Long pointId;
    private String orderId;
    private Long amount;
    private PointStatus status;

    public PointHistory(Long pointId,Long amount, String orderId, PointStatus status) {
        this.pointId = pointId;
        this.amount = amount;
        this.orderId = orderId;
        this.status = status;
    }

    static PointHistory charge(Long pointId, PointCommand.Charge command) {
        if(command.amount() <= 0) {
            throw new CoreException(ErrorType.INVALID_POINT_AMOUNT);
        }

        if(command.userId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 없습니다.");
        }

        if(pointId == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "포인트 ID가 없습니다.");
        }

        return new PointHistory(pointId, command.amount(),null, PointStatus.CHARGE);
    }

    static PointHistory use(Long pointId, PointCommand.Using command) {
        if(command.amount() <= 0) {
            throw new CoreException(ErrorType.INVALID_POINT_AMOUNT, "사용 금액은 0보다 커야 합니다.");
        }

        if(command.userId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 없습니다.");
        }

        if(command.orderId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 ID가 없습니다.");
        }

        if(pointId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트 ID가 없습니다.");
        }

        return new PointHistory(pointId, -command.amount(), command.orderId(), PointStatus.USE);
    }
}
