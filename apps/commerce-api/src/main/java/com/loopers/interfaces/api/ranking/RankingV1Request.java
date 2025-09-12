package com.loopers.interfaces.api.ranking;

import com.loopers.domain.ranking.RankingCommand;

public class RankingV1Request {
    public record GetRanking(
            String date
    ){
    }
}
