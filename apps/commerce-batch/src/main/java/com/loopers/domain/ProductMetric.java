package com.loopers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    LocalDate aggDate; // 날짜 기준
    @Column(nullable = false)
    Long productId;
    @Column(nullable = false)
    Long likeCount;
    @Column(nullable = false)
    Long orderQuantity;
    @Column(nullable = false)
    Long viewCount;

    public ProductMetric(LocalDate aggDate, Long productId, Long likeCount, Long orderQuantity, Long viewCount) {
        this.aggDate = aggDate;
        this.productId = productId;
        this.likeCount = likeCount;
        this.orderQuantity = orderQuantity;
        this.viewCount = viewCount;
    }

    public static ProductMetric create(Long productId, LocalDate aggDate) {
        return new ProductMetric(aggDate, productId, 0L, 0L, 0L);
    }

    public void updateLikeCount(Long delta) {
        this.likeCount += delta;
    }

    public void updateOrderQuantity(Long quantity) {
        this.orderQuantity += quantity;
    }

    public void updateViewCount(Long orderQuantity) {
        this.viewCount += orderQuantity;
    }

}
