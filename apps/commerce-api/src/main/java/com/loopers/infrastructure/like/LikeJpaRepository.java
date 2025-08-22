package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Long countByProductId(Long productId);

    Long countByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}
