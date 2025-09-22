package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.QMvProductRankMonthly;
import com.loopers.domain.ranking.QMvProductRankWeekly;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonthlyRankingQueryDslRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private static final QMvProductRankMonthly m = QMvProductRankMonthly.mvProductRankMonthly;

    public List<Long> getRanks(LocalDate aggDate, int page, int size) {
        int offset = (page - 1) * size;

        return jpaQueryFactory.select(m.productId)
                .from(m)
                .where(m.aggDate.eq(aggDate))
                .orderBy(m.totalScore.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }
}
