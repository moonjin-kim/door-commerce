package com.loopers.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class CalcDateInitializer implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jp = stepExecution.getJobParameters();
        String p = jp.getString("calcDate");
        LocalDate calcDate = (p != null && !p.isBlank())
                ? LocalDate.parse(p)
                : LocalDate.now(ZoneId.of("Asia/Seoul")); // ← 잡에서 기본값 결정

        // String으로 넣어두면 SpEL 주입이 편함
        stepExecution.getExecutionContext().put("calcDate", calcDate.toString());
    }
}
