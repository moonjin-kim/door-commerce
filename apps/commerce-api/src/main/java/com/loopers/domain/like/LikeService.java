package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public LikeInfo.SearchResult search(LikeQuery.Search likeSearch) {
        return likeRepository.search(likeSearch.toParams());
    }

    @Transactional
    public LikeInfo.AddLikeResult addLike(Long userId, Long productId) {
        if (likeRepository.existsBy(userId, productId)) {
            return LikeInfo.AddLikeResult.fail();
        }

        likeRepository.save(ProductLike.create(userId, productId));
        return LikeInfo.AddLikeResult.success();
    }

    @Transactional
    public LikeInfo.DeleteLikeResult unlike(Long userId, Long productId) {
        if (likeRepository.existsBy(userId, productId)) {
            likeRepository.delete(userId, productId);
            return LikeInfo.DeleteLikeResult.success();
        }

        return LikeInfo.DeleteLikeResult.fail();
    }
}
