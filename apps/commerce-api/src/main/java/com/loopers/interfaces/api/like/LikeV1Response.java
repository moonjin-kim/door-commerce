package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeResult;
import com.loopers.application.product.ProductResult;
import com.loopers.domain.product.ProductInfo;

public class LikeV1Response {
    public record LikeProduct(
            long productId,
            String name,
            String imageUrl,
            Long brandId,
            Long likeCount,
            boolean isLiked
    ) {
        public static LikeV1Response.LikeProduct of(LikeResult.LikeProduct product) {
            return new LikeV1Response.LikeProduct(
                    product.productId(),
                    product.name(),
                    product.imageUrl(),
                    product.brandId(),
                    0L,
                    true
            );
        }
    }
}
