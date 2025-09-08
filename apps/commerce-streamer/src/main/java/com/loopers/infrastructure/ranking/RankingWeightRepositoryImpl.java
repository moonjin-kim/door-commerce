package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingWeight;
import com.loopers.domain.ranking.RankingWeightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RankingWeightRepositoryImpl implements RankingWeightRepository {
    private final RankingJpaRepository rankingJpaRepository;

    @Override
    public RankingWeight save(RankingWeight rankingWeight) {
        return rankingJpaRepository.save(rankingWeight);
    }

    @Override
    public Optional<RankingWeight> findBy(String key) {
        return rankingJpaRepository.findByWeightName(key);
    }
}
