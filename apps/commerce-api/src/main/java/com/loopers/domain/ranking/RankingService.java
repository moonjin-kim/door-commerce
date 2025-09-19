package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingService {
    private final DaliyRankingRepository daliyRankingRepository;
    private final WeeklyRankingRepository weeklyRankingRepository;
    private final MonthlyRankingRepository monthlyRankingRepository;

    private static final DateTimeFormatter D8 = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public List<Long> getRanking(RankingCommand.GetRanking command) {
        switch (command.period()) {
            case DAILY -> {
                return daliyRankingRepository.getRanking(command.date(), command.page(), command.size());
            }
            case WEEKLY -> {
                return weeklyRankingRepository.getRanking(normalizeForRdb(command.date()), command.page(), command.size());
            }
            case MONTHLY -> {
                return monthlyRankingRepository.getRanking(normalizeForRdb(command.date()), command.page(), command.size());
            }
            default -> throw new IllegalArgumentException();
        }
    }

    public Long getRankBy(Long productId, String date) {
        return daliyRankingRepository.getRank(date, productId);
    }

    private LocalDate normalizeForRdb(String ymd) {
        return (ymd != null && !ymd.isBlank())
                ? LocalDate.parse(ymd, D8)
                : LocalDate.now(KST);
    }
}
