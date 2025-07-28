package com.loopers.domain.like;

public interface LikeRepository {
    boolean existsBy(Long userId, Long productId);

    ProductLike save(ProductLike productLike);

    void delete(Long userId, Long productId);
}
