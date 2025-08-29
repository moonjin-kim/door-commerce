package com.loopers.domain.payment;

import java.util.List;
import java.util.Optional;

public interface CommercePaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findAllBy(PaymentStatus status);
}
