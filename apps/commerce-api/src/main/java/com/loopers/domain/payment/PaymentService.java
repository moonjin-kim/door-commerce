package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final CommercePaymentRepository paymentRepository;
    private final PaymentEventPublisher eventPublisher;

    @Transactional
    public PaymentInfo.Pay createPayment(PaymentCommand.Pay command) {
        Payment payment = paymentRepository.save(Payment.create(
                command
        ));

        return PaymentInfo.Pay.from(payment);
    }

    @Transactional
    public PaymentInfo.Pay paymentComplete(String orderId, String transactionKey) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
        payment.complete(transactionKey);

        PaymentInfo.Pay paymentInfo = PaymentInfo.Pay.from(paymentRepository.save(payment));

        eventPublisher.publish(PaymentEvent.Success.of(orderId));

        return paymentInfo;
    }

    @Transactional
    public PaymentInfo.Pay paymentFail(String orderId, String reason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
        payment.fail(reason);

        PaymentInfo.Pay paymentInfo = PaymentInfo.Pay.from(paymentRepository.save(payment));

        System.out.println("########################### 결제 실패 이벤트 발행 ###########################");

        eventPublisher.publish(PaymentEvent.Failed.of(orderId));

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
