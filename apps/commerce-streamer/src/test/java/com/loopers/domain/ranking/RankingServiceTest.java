package com.loopers.domain.ranking;

import com.loopers.support.cache.CommerceCache;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RankingServiceTest {
    @Autowired
    RankingService rankingService;
    @Autowired
    RankingWeightRepository weightRepository;
    @Autowired
    RankingRepository rankingRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCleanUp redisCleanUp;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @DisplayName("랭킹을 갱신할 때")
    @Nested
    class UpdateProductScores {
        @DisplayName("랭킹 가중치가 존재하지 않으면 예외가 발생한다")
        @org.junit.jupiter.api.Test
        void shouldThrowExceptionWhenWeightNotFound() {
            RankingCommand.UpdateProductScore command = RankingCommand.UpdateProductScore.of(
                    1L, 10L, 5L, 2L, java.time.LocalDate.now());

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                rankingService.updateProductScore(command);
            });

            assertEquals("Ranking weight not found", exception.getMessage());
        }

        @DisplayName("랭킹 가중치가 존재하면 랭킹이 갱신된다")
        @Test
        void shouldUpdateRankingWhenWeightFound() {
            LocalDateTime now = LocalDateTime.now();
            Long productId = 1L;
            RankingWeight weight = RankingWeight.create("default",0.2, 0.5, 0.3);
            weightRepository.save("ranking:weight@v1", weight);

            RankingCommand.UpdateProductScore command = RankingCommand.UpdateProductScore.of(
                    productId, 10L, 5L, 2L, now.toLocalDate());

            assertDoesNotThrow(() -> {
                rankingService.updateProductScore(command);
            });
            double score = rankingRepository.getScoreBy(CommerceCache.RankingCache.INSTANCE, now.format(ofPattern("yyyyMMdd")), String.valueOf(productId));

            assertThat(score).isEqualTo(10L * 0.2 + 5L * 0.5 + 2L * 0.3);
        }
    }

    @DisplayName("")
    @Nested
    class CreateNextDayRanking {
        @Test
        @DisplayName("createNextDayRanking은 다음날 랭킹을 생성하고 TTL을 설정한다")
        void createNextDayRanking_shouldCopyAndSetTTL() {
            LocalDate today = LocalDate.now();
            String todayKey = CommerceCache.RankingCache.INSTANCE.getKey("ranking:daily:" + today.format(ofPattern("yyyyMMdd")));
            String tomorrowKey = CommerceCache.RankingCache.INSTANCE.getKey("ranking:daily:" + today.plusDays(1).format(ofPattern("yyyyMMdd")));

            // 오늘 랭킹 데이터 삽입
            redisTemplate.opsForZSet().add(todayKey, "101", 10.0);
            redisTemplate.opsForZSet().add(todayKey, "102", 20.0);

            // 다음날 랭킹 생성
            rankingService.createTomorrowRanking(today);

            // 다음날 랭킹 데이터 확인 (가중치 0.1이 곱해짐)
            Set<String> tomorrowRanking = redisTemplate.opsForZSet().range(tomorrowKey, 0, -1);
            assertThat(tomorrowRanking).contains("101", "102");

            Double score101 = redisTemplate.opsForZSet().score(tomorrowKey, "101");
            Double score102 = redisTemplate.opsForZSet().score(tomorrowKey, "102");
            assertThat(score101).isEqualTo(10.0 * 0.1);
            assertThat(score102).isEqualTo(20.0 * 0.1);

            // TTL 확인 (30일)
            Long ttl = redisTemplate.getExpire(tomorrowKey);
            assertThat(ttl).isGreaterThanOrEqualTo(Duration.ofDays(29).getSeconds());
        }
    }
}
