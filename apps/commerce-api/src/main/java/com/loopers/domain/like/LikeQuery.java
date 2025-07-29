package com.loopers.domain.like;

import com.loopers.domain.product.ProductQuery;
import com.loopers.infrastructure.like.LikeParams;
import com.loopers.infrastructure.product.ProductParams;

public class LikeQuery {
    public record Search(
            long userId
    ) {
        public static LikeQuery.Search of(long userId) {
            return new LikeQuery.Search(userId);
        }

        public LikeParams.Search toParams() {
            return LikeParams.Search.of(userId);
        }
    }
}
