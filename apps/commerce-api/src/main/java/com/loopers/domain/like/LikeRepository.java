package com.loopers.domain.like;

import com.loopers.infrastructure.like.LikeParams;

public interface LikeRepository {
    boolean existsBy(Long userId, Long productId);

    ProductLike save(ProductLike productLike);

    LikeInfo.SearchResult search(LikeParams.Search likeSearch);

    void delete(Long userId, Long productId);
}
