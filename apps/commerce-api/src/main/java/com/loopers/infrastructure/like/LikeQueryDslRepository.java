package com.loopers.infrastructure.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.ProductLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.domain.like.QProductLike.productLike;

@Component
@RequiredArgsConstructor
public class LikeQueryDslRepository implements LikeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageResponse<ProductLike> search(PageRequest<LikeParams.Search> searchParams) {
        List<ProductLike> items = jpaQueryFactory.selectFrom(productLike)
                .limit(searchParams.limit())
                .offset(searchParams.offset())
                .where(productLike.userId.eq(searchParams.getParams().userId()))
                .fetch();

        Long totalCount = jpaQueryFactory.select(productLike.count())
                .from(productLike)
                .where(productLike.userId.eq(searchParams.getParams().userId()))
                .fetchOne();

        long count = totalCount != null ? totalCount : 0L;


        return PageResponse.of(searchParams.getPage(), searchParams.getSize(), count, items);

    }
}
