package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductStatus;
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
    public LikeInfo.SearchResult search(LikeParams.Search likeSearch) {
        List<ProductLike> items = jpaQueryFactory.selectFrom(productLike)
                .limit(likeSearch.limit())
                .offset(likeSearch.offset())
                .where(productLike.userId.eq(likeSearch.userId()))
                .fetch();

        Long totalCount = jpaQueryFactory.select(productLike.count())
                .from(productLike)
                .where(productLike.userId.eq(likeSearch.userId()))
                .fetchOne();

        long count = totalCount != null ? totalCount : 0L;

        return new LikeInfo.SearchResult(likeSearch.limit(), likeSearch.offset(),
                count, items);
    }
}
