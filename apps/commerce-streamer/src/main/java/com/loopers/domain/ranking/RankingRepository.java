package com.loopers.domain.ranking;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RankingRepository {
    void updateProductRanking(String key, String productId, double score);
    Set<String> getRanking(String key, int start, int end);
}
