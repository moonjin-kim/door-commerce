package com.loopers.interfaces.api.ranking;

import com.loopers.domain.ranking.Period;
import com.loopers.domain.ranking.RankingCommand;

public class RankingV1Request {
    public enum PeriodType {
        DAILY, WEEKLY, MONTHLY
    }

    public record GetRanking(
            String date,
            PeriodType period
    ){
        public RankingCommand.GetRanking toCommand(int page, int size) {
            Period p = switch (period) {
                case DAILY -> Period.DAILY;
                case WEEKLY -> Period.WEEKLY;
                case MONTHLY -> Period.MONTHLY;
            };
            return RankingCommand.GetRanking.of(date, p, page, size);
        }
    }
}
