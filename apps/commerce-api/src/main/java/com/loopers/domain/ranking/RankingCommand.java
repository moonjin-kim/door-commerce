package com.loopers.domain.ranking;

public class RankingCommand {
    public record GetRanking(String date, Period period, int page, int size) {
        public static GetRanking of(String date,Period period, int page, int size) {
            return new GetRanking(date, period, page, size);
        }
    }
}
