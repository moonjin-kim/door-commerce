package com.loopers.application.like;

import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeQuery;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;

import java.util.List;

public class LikeResult {

    public record LikeProduct(
            long productId,
            String name,
            String imageUrl,
            Long brandId,
            Long likeCount,
            boolean isLiked
    ) {
        public static LikeProduct of(ProductInfo product) {
            return new LikeProduct(
                    product.id(),
                    product.name(),
                    product.imageUrl(),
                    product.brandId(),
                    0L,
                    true
            );
        }
    }
}
