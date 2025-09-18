package com.loopers.domain.ranking;

import java.util.List;

public interface DaliyRankingRepository {
    public List<Long> getRanking(String key, int page, int size);
    public Long getRank(String key, Long memberId);
}
