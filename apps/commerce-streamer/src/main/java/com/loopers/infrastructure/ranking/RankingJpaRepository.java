package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingWeight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingJpaRepository extends JpaRepository<RankingWeight, Long> {
    Optional<RankingWeight> findByWeightName(String weightName);
}
