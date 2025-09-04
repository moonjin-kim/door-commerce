package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.comman.CommonApplicationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeEventPublisher eventPublisher;

    // 예시: 만약 PersistenceException이 발생했다면
    @Transactional
    public void like(LikeCommand.Like command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            log.info("이미 좋아요가 존재합니다. userId={}, productId={}", command.userId(), command.productId());
            return;
        }
        String name = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("Like transaction ID {}", name);

        likeRepository.save(com.loopers.domain.like.Like.create(command));

        log.info("좋아요가 저장되었습니다. userId={}, productId={}", command.userId(), command.productId());
        eventPublisher.publish(LikeEvent.Like.of(command.productId()));
    }

    @Transactional
    public void unlike(LikeCommand.UnLike command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            likeRepository.delete(command.userId(), command.productId());

            eventPublisher.publish(LikeEvent.UnLike.of(command.productId()));
        }
    }

    public LikeInfo.IsLiked isLiked(LikeCommand.IsLiked command) {
        boolean exists = likeRepository.existsBy(command.userId(), command.productId());

        return LikeInfo.IsLiked.of(exists);
    }

    @Transactional(readOnly = true)
    public LikeInfo.GetLikeCount getLikeCount(Long productId) {
        Long count = likeRepository.countBy(productId);
        return LikeInfo.GetLikeCount.of(count);

    }

    public PageResponse<LikeInfo.Like> search(PageRequest<LikeQuery.Search> query) {
        PageResponse<Like> pageRequest = likeRepository.search(query.map(LikeQuery.Search::toParams));

        return pageRequest.map(LikeInfo.Like::of);
    }

    public Long getUserLikeCount(LikeQuery.SearchCount query) {
        return likeRepository.countByUserId(query.userId());
    }
}
