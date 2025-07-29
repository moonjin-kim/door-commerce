package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.like.LikeParams;

public interface LikeRepository {
    boolean existsBy(Long userId, Long productId);

    ProductLike save(ProductLike productLike);

    PageResponse<ProductLike> search(PageRequest<LikeParams.Search> likeSearch);

    void delete(Long userId, Long productId);
}
