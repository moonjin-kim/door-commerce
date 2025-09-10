package com.loopers.domain.ranking;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RankingRepository {
    void updateProductRankings(String key, Set<ZSetOperations.TypedTuple<String>> scores);
    void updateProductRanking(String key, String productId, double score);
    Set<String> getRanking(String key, int start, int end);
    double getScoreBy(String key, String productId);
}
