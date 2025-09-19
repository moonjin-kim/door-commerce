package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MonthlyRankingRepositoryImpl implements MonthlyRankingRepository {
    private final MonthlyRankingQueryDslRepository monthlyRankingQueryDslRepository;

    @Override
    public List<Long> getRanking(LocalDate date, int page, int size) {
        return monthlyRankingQueryDslRepository.getRanks(date, page, size);
    }
}
