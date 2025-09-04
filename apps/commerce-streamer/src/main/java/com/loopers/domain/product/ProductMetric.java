package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "product_metrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductMetric extends BaseEntity {
    @Column(nullable = false)
    LocalDate bucket; // 날짜 기준
    @Column(nullable = false)
    Long productId;
    @Column(nullable = false)
    Long likeCount;
    @Column(nullable = false)
    Long orderQuantity;
    @Column(nullable = false)
    Long viewCount;

    public ProductMetric(LocalDate bucket, Long productId, Long likeCount, Long orderQuantity, Long viewCount) {
        this.bucket = bucket;
        this.productId = productId;
        this.likeCount = likeCount;
        this.orderQuantity = orderQuantity;
        this.viewCount = viewCount;
    }

    public static ProductMetric create(Long productId, LocalDate bucket) {
        return new ProductMetric(bucket, productId, 0L, 0L, 0L);
    }

    public void updateLikeCount(Long delta) {
        this.likeCount += delta;
    }

    public void updateOrderQuantity(Long quantity) {
        this.orderQuantity += quantity;
    }

    public void plusViewCount() {
        this.viewCount++;
    }

}
