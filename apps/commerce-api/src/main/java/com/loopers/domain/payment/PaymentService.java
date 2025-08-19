package com.loopers.domain.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final CommercePaymentRepository paymentRepository;

    public PaymentService(@Qualifier("paymentRepositoryImpl") CommercePaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentInfo.Pay pay(PaymentCommand.Pay command) {
        Payment payment = Payment.create(
                command
        );

        return PaymentInfo.Pay.from(
                paymentRepository.save(payment)
        );
    }

    public PaymentInfo.Pay paymentComplete(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
        payment.complete();
        return PaymentInfo.Pay.from(paymentRepository.save(payment));
    }

    public PaymentInfo.Pay paymentFail(String orderId, String reason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
        payment.fail(reason);
        return PaymentInfo.Pay.from(paymentRepository.save(payment));
    }
}
