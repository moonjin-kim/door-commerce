package com.loopers.domain.like;

import com.loopers.domain.product.ProductQuery;
import com.loopers.infrastructure.like.LikeParams;
import com.loopers.infrastructure.product.ProductParams;

public class LikeQuery {
    public record Search(
            int limit,
            long offset,
            long userId
    ) {
        public static LikeQuery.Search of(int limit, long offset, long userId) {
            return new LikeQuery.Search(limit, offset, userId);
        }

        public LikeParams.Search toParams() {
            return LikeParams.Search.of(limit, offset, userId);
        }
    }
}
