package com.loopers.domain.product;

import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품을 조회할 때")
    @Nested
    class FindBy {
        @DisplayName("존재하지 않는 상품 아이디가 주어지면 빈 Optional을 반환한다.")
        @Test
        void returnEmpty_whenProductIsNotFound(){
            //given
            Long productId = 1L;

            //when
            Optional<Product> foundProduct = productService.getBy(productId);

            //then
            assertAll(
                    () -> assertFalse(foundProduct.isPresent())
            );
        }

        @DisplayName("조회할 상품의 아이디가 주어지면 해당 상품을 반환한다.")
        @Test
        void returnProduct_whenProductIdIsProvider(){
            //given
            var product = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            20000L
                    ))
            );
            Long productId = 1L;

            //when
            Optional<Product> foundProduct = productService.getBy(productId);

            //then
            assertAll(
                    () -> assertTrue(foundProduct.isPresent()),
                    () -> assertEquals(product, foundProduct.get())
            );
        }
    }
}
