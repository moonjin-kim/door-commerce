package com.loopers.domain.ranking;

import com.loopers.support.cache.CacheKey;

import java.util.List;
import java.util.Set;

public interface RankingRepository {
    public Set<String> getRanking(CacheKey cache, String key, int page, int size);
    public Long getRank(CacheKey cache, String key, Long memberId);
}
