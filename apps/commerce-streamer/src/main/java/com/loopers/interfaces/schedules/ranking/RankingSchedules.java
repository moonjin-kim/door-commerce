package com.loopers.interfaces.schedules.ranking;

import com.loopers.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RankingSchedules {

    private final RankingService rankingService;

    @Scheduled(cron = "0 50 23 * * *")
    public void processPendingOrders() {
        rankingService.createTomorrowRanking(LocalDate.now());
    }
}
