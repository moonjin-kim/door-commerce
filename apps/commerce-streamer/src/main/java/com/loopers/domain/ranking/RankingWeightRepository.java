package com.loopers.domain.ranking;

import java.util.Optional;

public interface RankingWeightRepository {
    RankingWeight save(String key, RankingWeight rankingWeight);
    Optional<RankingWeight> findBy(String key);
}
