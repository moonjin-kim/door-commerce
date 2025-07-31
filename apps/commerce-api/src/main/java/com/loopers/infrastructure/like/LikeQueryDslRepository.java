package com.loopers.infrastructure.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.Like;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.domain.like.QLike.like;

@Component
@RequiredArgsConstructor
public class LikeQueryDslRepository implements LikeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageResponse<Like> search(PageRequest<LikeParams.Search> searchParams) {
        List<Like> items = jpaQueryFactory.selectFrom(like)
                .limit(searchParams.limit())
                .offset(searchParams.offset())
                .where(like.userId.eq(searchParams.getParams().userId()))
                .fetch();

        Long totalCount = jpaQueryFactory.select(like.count())
                .from(like)
                .where(like.userId.eq(searchParams.getParams().userId()))
                .fetchOne();

        long count = totalCount != null ? totalCount : 0L;


        return PageResponse.of(searchParams.getPage(), searchParams.getSize(), count, items);

    }
}
