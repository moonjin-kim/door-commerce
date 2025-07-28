package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductLike extends BaseEntity {
    @Column(nullable = false)
    Long userId;
    @Column(nullable = false)
    Long productId;

    protected ProductLike(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    static ProductLike create(Long userId, Long productId) {
        return new ProductLike(userId, productId);
    }
}
