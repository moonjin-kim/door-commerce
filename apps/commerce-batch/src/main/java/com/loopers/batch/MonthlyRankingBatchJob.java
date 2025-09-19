package com.loopers.batch;

import com.loopers.domain.ProductMetric;
import com.loopers.domain.ranking.MonthlyProductMetricWriter;
import com.loopers.domain.ranking.WeeklyRankRow;
import com.loopers.domain.step.MonthlyProductMetricReader;
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
public class MonthlyRankingBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job monthlyRankingJob(@Qualifier("monthlyRankingStep") Step monthlyRankingStep) {
        return new JobBuilder("monthlyRankingJob", jobRepository)
                .start(monthlyRankingStep)
                .build();
    }

    @Bean
    public Step monthlyRankingStep(
            MonthlyProductMetricReader monthlyProductMetricReader,
            ProductAggToRankProcessor productAggToRankProcessor,
            MonthlyProductMetricWriter monthlyProductMetricWriter,
            CalcDateInitializer calcDateInitializer
    ) {
        return new StepBuilder("monthlyRankingJob", jobRepository)
                .<ProductMetric, WeeklyRankRow>chunk(1000, transactionManager)
                .reader(monthlyProductMetricReader)
                .processor(productAggToRankProcessor)
                .writer(monthlyProductMetricWriter)
                .listener(calcDateInitializer)
                .build();
    }

}
