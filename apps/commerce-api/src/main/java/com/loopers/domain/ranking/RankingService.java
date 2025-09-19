package com.loopers.domain.ranking;

import com.loopers.support.cache.CommerceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RankingService {
    private final RankingRepository rankingRepository;

    public Set<String> getRanking(RankingCommand.GetRanking command) {
        return rankingRepository.getRanking(CommerceCache.RankingCache.INSTANCE, command.date(), command.page(), command.size());
    }

    public Long getRankBy(Long productId, String date) {
        return rankingRepository.getRank(CommerceCache.RankingCache.INSTANCE, date, productId);
    }
}
