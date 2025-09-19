package com.loopers.batch;

import com.loopers.domain.ProductMetric;
import com.loopers.domain.ranking.WeeklyProductMetricWriter;
import com.loopers.domain.ranking.WeeklyRankRow;
import com.loopers.domain.step.ProductMetricReader;
import com.loopers.domain.step.ProductAggToRankProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WeeklyRankingBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job weeklyRankingJob(@Qualifier("weeklyRankingStep") Step weeklyRankingStep) {
        return new JobBuilder("weeklyRankingJob", jobRepository)
                .start(weeklyRankingStep)
                .build();
    }

    @Bean
    public Step weeklyRankingStep(
            ProductMetricReader weeklyAggReader,
            ProductAggToRankProcessor weeklyAggToRankProcessor,
            WeeklyProductMetricWriter topKUpsertWriter,
            CalcDateInitializer calcDateInitializer
    ) {
        return new StepBuilder("weeklyAggregateStep", jobRepository)
                .<ProductMetric, WeeklyRankRow>chunk(1000, transactionManager)
                .reader(weeklyAggReader)
                .processor(weeklyAggToRankProcessor)
                .writer(topKUpsertWriter)
                .listener(calcDateInitializer)
                .build();
    }

}
