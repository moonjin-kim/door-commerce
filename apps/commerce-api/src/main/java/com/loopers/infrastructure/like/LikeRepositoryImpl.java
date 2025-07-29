package com.loopers.infrastructure.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeQuery;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
    private final LikeJpaRepository likeJpaRepository;
    private final LikeCustomRepository likeCustomRepository;

    @Override
    public ProductLike save(ProductLike productLike) {
        return likeJpaRepository.save(productLike);
    }


    @Override
    public PageResponse<ProductLike> search(PageRequest<LikeParams.Search> likeSearch) {
        return likeCustomRepository.search(likeSearch);
    }

    @Override
    public boolean existsBy(Long userId, Long productId) {
        return likeJpaRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public void delete(Long userId, Long productId) {
        likeJpaRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
