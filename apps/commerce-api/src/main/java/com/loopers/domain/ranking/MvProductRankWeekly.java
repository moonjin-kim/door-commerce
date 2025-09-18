package com.loopers.domain.ranking;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(
        name = "mv_product_rank_weekly",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aggDate", "productId"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MvProductRankWeekly{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id = 0L;
    @Column(nullable = false)
    LocalDate aggDate; // 날짜 기준
    @Column(nullable = false)
    Long productId;
    @Column(nullable = false)
    private BigDecimal totalScore;
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public MvProductRankWeekly(LocalDate aggDate, Long productId, BigDecimal totalScore) {
        this.aggDate = aggDate;
        this.productId = productId;
        this.totalScore = totalScore;
    }
}
