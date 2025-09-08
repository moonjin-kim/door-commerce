package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ranking_weight")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RankingWeight extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String weightName;
    @Column(nullable = false)
    private Double likeWeight;
    @Column(nullable = false)
    private Double orderWeight;
    @Column(nullable = false)
    private Double viewWeight;

    public RankingWeight(String weightName, Double likeWeight, Double orderWeight, Double viewWeight) {
        if(likeWeight < 0 || orderWeight < 0 || viewWeight < 0) {
            throw new IllegalArgumentException("Weights must be non-negative");
        }
        if(weightName == null || weightName.isEmpty()) {
            throw new IllegalArgumentException("Weight name must not be null or empty");
        }
        this.weightName = weightName;
        this.likeWeight = likeWeight;
        this.orderWeight = orderWeight;
        this.viewWeight = viewWeight;
    }

    static public RankingWeight create(String weightName, Double likeWeight, Double orderWeight, Double viewWeight) {
        return new RankingWeight(
                weightName, likeWeight, orderWeight, viewWeight
        );
    }

    public double calculateScore(RankingCommand.UpdateProductScore command) {
        return command.likeCount() * likeWeight + command.orderQuantity() * orderWeight + command.viewCount() * viewWeight;
    }
}
