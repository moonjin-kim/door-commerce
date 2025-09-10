package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Component
@RequiredArgsConstructor
public class RankingService {
    private final String WEIGHT_KEY = "default";
    private final RankingWeightRepository rankingWeightRepository;
    private final RankingRepository rankingRepository;

    public void updateProductScore(RankingCommand.UpdateProductScore command) {
        RankingWeight rankingWeight = rankingWeightRepository.findBy(WEIGHT_KEY)
            .orElseThrow(() -> new IllegalStateException("Ranking weight not found"));

        double newScore = rankingWeight.calculateScore(command);

        rankingRepository.updateProductRanking(command.date(), String.valueOf(command.productId()), newScore);
    }

    public void updateProductScores(List<RankingCommand.UpdateProductScores> commands, LocalDate date) {
        RankingWeight rankingWeight = rankingWeightRepository.findBy(WEIGHT_KEY)
                .orElseThrow(() -> new IllegalStateException("Ranking weight not found"));

        Set<ZSetOperations.TypedTuple<String>> scoreTuple = commands.stream()
                .map(command -> {
                    double newScore = rankingWeight.calculateScore(command);
                    return new DefaultTypedTuple<>(String.valueOf(command.productId()), newScore);
                })
                .collect(Collectors.toSet());

        if(!scoreTuple.isEmpty()) {
            rankingRepository.updateProductRankings(date.format(ofPattern("yyyyMMdd")), scoreTuple);
        }
    }
}
