package com.loopers.application.order;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSchedules {


    @Scheduled(fixedRate = 10000)
    public void processPendingOrders() {
        System.out.println("Processing pending orders...");
    }
}
