package com.loopers.domain.point;

import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

public interface PointHistoryRepository {
    PointHistory save(PointHistory pointHistory);

    Optional<PointHistory> findByOrderId(String orderId);
}
