package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentInfo.Pay pay(PaymentCommand.Pay command) {
        Payment payment = Payment.create(
                command
        );

        return PaymentInfo.Pay.from(
                paymentRepository.save(payment)
        );
    }
}
