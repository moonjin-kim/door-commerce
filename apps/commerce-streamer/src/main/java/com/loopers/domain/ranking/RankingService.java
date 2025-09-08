package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingService {
    private final String WEIGHT_KEY = "default";
    private final RankingWeightRepository rankingWeightRepository;
    private final RankingRepository rankingRepository;

    public void updateProductScores(RankingCommand.UpdateProductScore command) {
        RankingWeight rankingWeight = rankingWeightRepository.findBy(WEIGHT_KEY)
            .orElseThrow(() -> new IllegalStateException("Ranking weight not found"));

        double newScore = rankingWeight.calculateScore(command);

        rankingRepository.updateProductRanking(command.date().toString(), String.valueOf(command.productId()), newScore);
    }
}
