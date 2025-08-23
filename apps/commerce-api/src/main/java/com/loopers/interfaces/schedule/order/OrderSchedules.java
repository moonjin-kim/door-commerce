package com.loopers.interfaces.schedule.order;

import com.loopers.application.order.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderSchedules {
    private final OrderFacade orderFacade;

    @Scheduled(fixedRate = 60000)
    public void processPendingOrders() {
        orderFacade.syncPayment(LocalDateTime.now());
    }
}
