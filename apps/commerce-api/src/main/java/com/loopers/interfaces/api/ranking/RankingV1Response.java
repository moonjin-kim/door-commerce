package com.loopers.interfaces.api.ranking;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;

public class RankingV1Response {
    public record ProductDto(
            Long productId,
            String name,
            String imageUrl,
            Long price
    ) {
        public static ProductDto from(Product product) {
            return new ProductDto(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getPrice().longValue()
            );
        }
    }
}
