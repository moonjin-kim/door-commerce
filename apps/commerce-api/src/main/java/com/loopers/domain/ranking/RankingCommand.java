package com.loopers.domain.ranking;

public class RankingCommand {
    public record GetRanking(String date, int page, int size) {
        public static GetRanking of(String date, int page, int size) {
            return new GetRanking(date, page, size);
        }
    }
}
