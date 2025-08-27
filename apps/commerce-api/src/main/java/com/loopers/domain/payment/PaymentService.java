package com.loopers.domain.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CommercePaymentRepository paymentRepository;
    private final PaymentEventPublisher eventPublisher;

//    public PaymentService(@Qualifier("paymentRepositoryImpl") CommercePaymentRepository paymentRepository) {
//        this.paymentRepository = paymentRepository;
//    }

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

        PaymentInfo.Pay paymentInfo = PaymentInfo.Pay.from(paymentRepository.save(payment));

        eventPublisher.publish(PaymentEvent.Success.of(orderId));

        return paymentInfo;
    }

    public PaymentInfo.Pay paymentFail(String orderId, String reason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
        payment.fail(reason);

        PaymentInfo.Pay paymentInfo = PaymentInfo.Pay.from(paymentRepository.save(payment));

        eventPublisher.publish(PaymentEvent.Success.of(orderId));

        return paymentInfo;
    }

    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPendingOrders() {
        // 주문 목록 조회
        return paymentRepository.findAllBy(PaymentStatus.PENDING);
    }
}
