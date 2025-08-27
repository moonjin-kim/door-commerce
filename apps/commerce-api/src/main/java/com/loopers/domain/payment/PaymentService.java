package com.loopers.domain.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final CommercePaymentRepository paymentRepository;

    public PaymentService(@Qualifier("paymentRepositoryImpl") CommercePaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentInfo.Pay requestPayment(PaymentCommand.Pay command) {
        Payment payment = Payment.create(
                command
        );

        return PaymentInfo.Pay.from(
                paymentRepository.save(payment)
        );
    }

    public PaymentInfo.Pay paymentComplete(String orderId, String transactionKey) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
        payment.complete(transactionKey);

        return PaymentInfo.Pay.from(paymentRepository.save(payment));
    }

    public PaymentInfo.Pay paymentFail(String orderId, String reason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
        payment.fail(reason);

        return PaymentInfo.Pay.from(paymentRepository.save(payment));
    }

    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPendingOrders() {
        // 주문 목록 조회
        return paymentRepository.findAllBy(PaymentStatus.PENDING);
    }
}
