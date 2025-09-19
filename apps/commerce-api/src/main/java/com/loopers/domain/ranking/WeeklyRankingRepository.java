package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface WeeklyRankingRepository {
    List<Long> getRanking(LocalDate date, int page, int size);
}
