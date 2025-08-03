package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;

import java.time.LocalDateTime;

public class Payment extends BaseEntity {
    Long orderId;
    Long userId;
    Money usedPoint;
    Money paymentAmount;
    Money totalAmount;
    String status;
    LocalDateTime paymentDate;
}
