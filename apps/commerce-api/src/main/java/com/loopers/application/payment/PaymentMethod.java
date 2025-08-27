package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentInfo;

public interface PaymentMethod {
    PaymentInfo.Pay pay(PaymentCriteria.RequestPayment command);
}
