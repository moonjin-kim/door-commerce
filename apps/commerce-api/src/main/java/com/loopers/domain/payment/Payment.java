package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseEntity {
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Money paymentAmount;
    @Column(nullable = false)
    private PaymentStatus status;
    @Column(nullable = false)
    private PaymentType paymentType;
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    protected Payment(
            String orderId,
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
        this.paymentAmount = new Money(new BigDecimal(paymentAmount));
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
                PaymentStatus.PENDING,
                PaymentType.of(command.method()),
                LocalDateTime.now()
        );
    }

    public void complete() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제 상태가 PENDING이 아닙니다.");
        }
        this.status = PaymentStatus.COMPLETED;
    }
}
