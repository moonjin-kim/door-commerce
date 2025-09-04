package com.loopers.domain.product;

import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductMetricServiceTest {

    @Autowired
    private ProductMetricService productMetricService;
    @Autowired
    private ProductMetricRepository productMetricRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    @DisplayName("save 메서드는 ProductMetric을 저장한다")
    void save_shouldPersistProductMetric() {
        ProductMetric metric = ProductMetric.create(1L, LocalDate.now());
        productMetricService.save(metric);

        Optional<ProductMetric> found = productMetricRepository.findBy(1L, LocalDate.now());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("updateLikeCount는 좋아요 수를 변경한다")
    void updateLikeCount_shouldUpdateLikeCount() {
        Long productId = 2L;
        LocalDate date = LocalDate.now();
        ProductMetric metric = productMetricService.save(ProductMetric.create(productId, date));

        ProductMetricCommand.LikeChange command = ProductMetricCommand.LikeChange.of(productId, date, 5L);
        productMetricService.updateLikeCount(command);

        ProductMetric updated = productMetricRepository.findBy(productId, date).orElseThrow();
        assertThat(updated.getLikeCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("updateOrderQuantity는 주문 수량을 변경한다")
    void updateOrderQuantity_shouldUpdateOrderQuantity() {
        Long productId = 3L;
        LocalDate date = LocalDate.now();
        productMetricService.save(ProductMetric.create(productId, date));

        ProductMetricCommand.StockChange command = ProductMetricCommand.StockChange.of(productId, date, 10L);
        productMetricService.updateOrderQuantity(command);

        ProductMetric updated = productMetricRepository.findBy(productId, date).orElseThrow();
        assertThat(updated.getOrderQuantity()).isEqualTo(10L);
    }

    @Test
    @DisplayName("updateViewCount는 조회수를 1 증가시킨다")
    void updateViewCount_shouldIncreaseViewCount() {
        Long productId = 4L;
        LocalDate date = LocalDate.now();
        productMetricService.save(ProductMetric.create(productId, date));

        ProductMetricCommand.ViewChange command = ProductMetricCommand.ViewChange.of(productId, date);
        productMetricService.updateViewCount(command);

        ProductMetric updated = productMetricRepository.findBy(productId, date).orElseThrow();
        assertThat(updated.getViewCount()).isEqualTo(1L);
    }
}
