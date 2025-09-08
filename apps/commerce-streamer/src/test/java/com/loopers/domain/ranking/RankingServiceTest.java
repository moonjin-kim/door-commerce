package com.loopers.domain.ranking;

import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

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
                rankingService.updateProductScores(command);
            });

            assertEquals("Ranking weight not found", exception.getMessage());
        }

        @DisplayName("랭킹 가중치가 존재하면 랭킹이 갱신된다")
        @Test
        void shouldUpdateRankingWhenWeightFound() {
            LocalDateTime now = LocalDateTime.now();
            Long productId = 1L;
            RankingWeight weight = RankingWeight.create("default",0.2, 0.5, 0.3);
            weightRepository.save(weight);

            RankingCommand.UpdateProductScore command = RankingCommand.UpdateProductScore.of(
                    productId, 10L, 5L, 2L, now.toLocalDate());

            assertDoesNotThrow(() -> {
                rankingService.updateProductScores(command);
            });
            double score = rankingRepository.getScoreBy(String.valueOf(now.toLocalDate()), String.valueOf(productId));

            assertThat(score).isEqualTo(10L * 0.2 + 5L * 0.5 + 2L * 0.3);
        }
    }
}
