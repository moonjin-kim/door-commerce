package com.loopers.infrastructure.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
    private final LikeJpaRepository likeJpaRepository;
    private final LikeCustomRepository likeCustomRepository;

    @Override
    public Like save(Like like) {
        return likeJpaRepository.save(like);
    }


    @Override
    public PageResponse<Like> search(PageRequest<LikeParams.Search> likeSearch) {
        return likeCustomRepository.search(likeSearch);
    }

    @Override
    public Long countBy(Long productId) {
        return likeJpaRepository.countByProductId(productId);
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
