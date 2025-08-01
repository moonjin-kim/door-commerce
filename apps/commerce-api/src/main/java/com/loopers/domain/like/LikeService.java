package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public PageResponse<LikeInfo.Like> search(PageRequest<LikeQuery.Search> query) {
        PageResponse<Like> pageRequest = likeRepository.search(query.map(LikeQuery.Search::toParams));

        return pageRequest.map(LikeInfo.Like::of);
    }

    @Transactional
    public LikeInfo.LikeResult like(LikeCommand.Like command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            return LikeInfo.LikeResult.fail();
        }

        likeRepository.save(Like.create(command));
        return LikeInfo.LikeResult.success();
    }

    public LikeInfo.IsLiked isLiked(LikeCommand.IsLiked command) {
        boolean exists = likeRepository.existsBy(command.userId(), command.productId());

        return LikeInfo.IsLiked.of(exists);
    }

    @Transactional
    public LikeInfo.UnLikeResult unlike(LikeCommand.UnLike command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            likeRepository.delete(command.userId(), command.productId());
            return LikeInfo.UnLikeResult.success();
        }

        return LikeInfo.UnLikeResult.fail();
    }

    @Transactional(readOnly = true)
    public LikeInfo.GetLikeCount getLikeCount(Long productId) {
        Long count = likeRepository.countBy(productId);
        return LikeInfo.GetLikeCount.of(count);

    }
}
