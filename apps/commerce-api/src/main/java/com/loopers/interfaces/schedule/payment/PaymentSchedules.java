package com.loopers.interfaces.schedule.payment;

import com.loopers.application.payment.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentSchedules {
    private final PaymentFacade paymentFacade;

    @Scheduled(fixedRate = 60000)
    public void processPendingOrders() {
        paymentFacade.syncPayment(LocalDateTime.now());
    }
}
