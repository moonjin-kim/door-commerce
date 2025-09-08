package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import com.loopers.support.cache.CacheKey;
import com.loopers.support.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {
    private final CacheRepository cacheRepository;

    @Override
    public Set<String> getRanking(CacheKey cache, String key, int startIndex, int endIndex) {
        return cacheRepository.zrevrange(cache, key, startIndex, endIndex);
    }

    @Override
    public Long getRank(CacheKey cache, String key, Long memberId) {
        return cacheRepository.getRank(cache, key, String.valueOf(memberId));
    }
}
