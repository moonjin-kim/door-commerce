package com.loopers.application.order.payment;

import com.loopers.domain.payment.PaymentInfo;

public interface PaymentMethod {
    PaymentInfo.Pay pay(PaymentCriteria.Pay command);
}
