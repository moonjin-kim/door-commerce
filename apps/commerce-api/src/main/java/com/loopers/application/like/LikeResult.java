package com.loopers.application.like;

import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeQuery;
import com.loopers.domain.product.Product;

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
        public static LikeProduct of(Product product) {
            return new LikeProduct(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getBrandId(),
                    product.getLikeCount(),
                    true
            );
        }
    }
}
