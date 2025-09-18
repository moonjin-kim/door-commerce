package com.loopers.interfaces.schedules;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RankingSchedules {
    private final JobLauncher jobLauncher;
    private final Job weeklyRankingJob;
    private final Job monthlyRankingJob;

    public RankingSchedules(JobLauncher jobLauncher,
                            @Qualifier("weeklyRankingJob") Job weeklyRankingJob,
                            @Qualifier("monthlyRankingJob") Job monthlyRankingJob) {
        this.jobLauncher = jobLauncher;
        this.weeklyRankingJob = weeklyRankingJob;
        this.monthlyRankingJob = monthlyRankingJob;
    }

    // 주간 랭킹 매주 월요일 새벽 1시 실행
    @Scheduled(cron = "0 30 0 * * *", zone = "Asia/Seoul")
    public void runWeeklyRankingJob() throws Exception {
        jobLauncher.run(
                weeklyRankingJob,
                new JobParametersBuilder()
                        .addString("calcDate", LocalDate.now().toString())
                        .addLong("time", System.currentTimeMillis()) // 매번 다른 파라미터 필요
                        .toJobParameters()
        );
    }

    // 월간 랭킹 매월 1일 새벽 2시 실행
    @Scheduled(cron = "0 30 0 * * *", zone = "Asia/Seoul")
    public void runMonthlyRankingJob() throws Exception {
        jobLauncher.run(
                monthlyRankingJob,
                new JobParametersBuilder()
                        .addString("calcDate", LocalDate.now().toString())
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters()
        );
    }
}
