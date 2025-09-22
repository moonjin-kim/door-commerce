package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.WeeklyRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WeeklyRankingRepositoryImpl implements WeeklyRankingRepository {
    private final WeeklyRankingQueryDslRepository weeklyRankingQueryDslRepository;

    @Override
    public List<Long> getRanking(LocalDate key, int page, int size) {
        return weeklyRankingQueryDslRepository.getRanks(key, page, size);
    }
}
