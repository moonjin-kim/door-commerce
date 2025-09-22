package com.loopers.domain.ranking;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeeklyRankRow(LocalDate aggDate, long productId, BigDecimal score) {}
