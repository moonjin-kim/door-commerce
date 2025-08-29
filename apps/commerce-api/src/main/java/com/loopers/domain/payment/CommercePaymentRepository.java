package com.loopers.domain.payment;

import java.util.Optional;

public interface CommercePaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByOrderId(String orderId);
}
