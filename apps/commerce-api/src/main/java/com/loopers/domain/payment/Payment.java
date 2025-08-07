package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseEntity {
    private Long orderId;
    private Long userId;
    private Money paymentAmount;
    private PaymentStatus status;
    private PaymentType paymentType;
    private LocalDateTime paymentDate;

    protected Payment(
            Long orderId,
            Long userId,
            Long paymentAmount,
            PaymentStatus status,
            PaymentType paymentType,
            LocalDateTime paymentDate
    ) {
        if(orderId == null) {
            throw new IllegalArgumentException("Order ID는 null일 수 없습니다.");
        }
        if(userId == null) {
            throw new IllegalArgumentException("User ID는 null일 수 없습니다.");
        }
        if(paymentAmount == null || paymentAmount < 0) {
            throw new IllegalArgumentException("결제 금액은 0 이상이여야 합니다.");
        }

        this.orderId = orderId;
        this.userId = userId;
        this.paymentAmount = new Money(paymentAmount);
        this.status = status;
        this.paymentType = paymentType;
        this.paymentDate = paymentDate;
    }

    public static Payment create(
            PaymentCommand.Pay command
    ) {
        return new Payment(
                command.orderId(),
                command.userId(),
                command.amount(),
                PaymentStatus.COMPLETED,
                PaymentType.of(command.method()),
                LocalDateTime.now()
        );
    }
}
