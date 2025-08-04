package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Like extends BaseEntity {
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long productId;

    protected Like(Long userId, Long productId) {
        if(userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 비어있을 수 없습니다.");
        }
        this.userId = userId;
        if(productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 비어있을 수 없습니다.");
        }
        this.productId = productId;
    }

    public static Like create(LikeCommand.Like command) {
        return new Like(command.userId(), command.productId());
    }
}
