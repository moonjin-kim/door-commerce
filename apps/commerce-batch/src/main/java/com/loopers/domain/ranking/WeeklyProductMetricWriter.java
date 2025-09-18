package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
@RequiredArgsConstructor
public class WeeklyProductMetricWriter implements ItemStreamWriter<WeeklyRankRow> {
    private final JdbcTemplate jdbc;
    // 필요하면 파라미터로 테이블명 주입
    @Value("#{jobParameters['tableName'] ?: 'mv_product_rank_weekly'}")
    private String tableName;

    /**
     * 청크마다 즉시 UPSERT (합산):
     * - 없으면 INSERT
     * - 있으면 total_score = total_score + VALUES(total_score)
     */
    @Override
    @Transactional
    public void write(Chunk<? extends WeeklyRankRow> chunk) {
        if (chunk.isEmpty()) return;

        Map<Long, BigDecimal> scoreByProduct = new HashMap<>();
        for (WeeklyRankRow pm : chunk) {
            scoreByProduct.merge(pm.productId(), pm.score(), BigDecimal::add);
        }

        final String sql = """
            INSERT INTO %s (agg_date, product_id, total_score, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
              total_score = total_score + VALUES(total_score),
              updated_at  = NOW()
            """.formatted(tableName);

        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                WeeklyRankRow r = chunk.getItems().get(i);
                ps.setObject(1, r.aggDate());      // 또는 r.aggDate() - DTO 필드명에 맞추세요
                ps.setLong(2, r.productId());
                ps.setBigDecimal(3, r.score());     // 합산할 증분값(delta)
            }
            @Override
            public int getBatchSize() {
                return chunk.size();
            }
        });
    }
}
