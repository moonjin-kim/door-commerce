package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class RankingWeight {
    private Double likeWeight;
    private Double orderWeight;
    private Double viewWeight;

    public RankingWeight(Double likeWeight, Double orderWeight, Double viewWeight) {
        if(likeWeight < 0 || orderWeight < 0 || viewWeight < 0) {
            throw new IllegalArgumentException("Weights must be non-negative");
        }
        this.likeWeight = likeWeight;
        this.orderWeight = orderWeight;
        this.viewWeight = viewWeight;
    }

    static public RankingWeight create(Double likeWeight, Double orderWeight, Double viewWeight) {
        return new RankingWeight(likeWeight, orderWeight, viewWeight
        );
    }

    public double calculateScore(RankingCommand.UpdateProductScore command) {
        return command.likeCount() * likeWeight + command.orderQuantity() * orderWeight + command.viewCount() * viewWeight;
    }

    public double calculateScore(RankingCommand.UpdateProductScores command) {
        return command.likeCount() * likeWeight + command.orderQuantity() * orderWeight + command.viewCount() * viewWeight;
    }
}
