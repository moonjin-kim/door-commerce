package com.loopers.domain.product;

import com.loopers.application.product.ProductResult;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @DisplayName("상품을 검색할 때")
    @Nested
    class Search {
        @DisplayName("검색 조건이 주어지면 해당 조건에 맞는 상품 페이지를 반환한다.")
        @Test
        void returnProductPage_whenSearchQueryIsProvided() {
            //given
            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            20000L
                    ))
            );
            var product2 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 후드티",
                            "루퍼스의 공식 후드티입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/hoodie.png",
                            30000L
                    ))
            );

            ProductQuery.Search query = new ProductQuery.Search(10,0,null, null);

            //when
            ProductInfo.ProductPage productPage = productService.search(query);

            //then
            assertAll(
                    () -> assertThat(productPage.totalElements()).isEqualTo(2),
                    () -> assertThat(productPage.limit()).isEqualTo(10),
                    () -> assertThat(productPage.offset()).isEqualTo(0),
                    () -> assertThat(productPage.items()).hasSize(2)
            );
        }

        @DisplayName("특정 브랜드ID가 주어지면 해당 브랜드의 상품 페이지를 반환한다.")
        @Test
        void returnProductPage_when() {
            //given
            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            20000L
                    ))
            );
            var product2 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            2L,
                            "루퍼스 공식 후드티",
                            "루퍼스의 공식 후드티입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/hoodie.png",
                            30000L
                    ))
            );

            ProductQuery.Search query = new ProductQuery.Search(10,0,null, 1L);

            //when
            ProductInfo.ProductPage productPage = productService.search(query);

            //then
            assertAll(
                    () -> assertThat(productPage.totalElements()).isEqualTo(1),
                    () -> assertThat(productPage.limit()).isEqualTo(10),
                    () -> assertThat(productPage.offset()).isEqualTo(0),
                    () -> assertThat(productPage.items()).hasSize(1),
                    () -> assertThat(productPage.items().get(0).getId()).isEqualTo(ProductResult.ProductDto.of(product1).id())
            );
        }

        @DisplayName("가격순으로 정렬 옵션이 주어지면 상품 리스트가 가격순으로 정렬된다.")
        @Test
        void returnProductPage_whenPriceAscIsProvicer() {
            //given
            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            20000L
                    ))
            );
            var product2 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            2L,
                            "루퍼스 공식 후드티",
                            "루퍼스의 공식 후드티입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/hoodie.png",
                            10000L
                    ))
            );

            ProductQuery.Search query = new ProductQuery.Search(10,0,"price_asc", null);

            //when
            ProductInfo.ProductPage productPage = productService.search(query);

            //then
            assertAll(
                    () -> assertThat(productPage.totalElements()).isEqualTo(2),
                    () -> assertThat(productPage.limit()).isEqualTo(10),
                    () -> assertThat(productPage.offset()).isEqualTo(0),
                    () -> assertThat(productPage.items()).hasSize(2),
                    () -> assertThat(productPage.items().get(0).getId()).isEqualTo(ProductResult.ProductDto.of(product2).id())
            );
        }
    }

    @DisplayName("좋아요 수 증가 기능을 테스트할 때")
    @Nested
    class IncreaseLikeCount {
        @DisplayName("상품이 존재하면 상품의 좋아요 수를 증가시킨다.")
        @Test
        void increaseLikeCount_whenProductExists() {
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

            //when
            productService.increaseLikeCount(product.getId());

            //then
            Product findProduct = productJpaRepository.findById(product.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findProduct.getLikeCount()).isEqualTo(1)
            );
        }

        @DisplayName("존재하지 않는 상품의 좋아요 수를 증가시키면 예외가 발생한다.")
        @Test
        void throwException_whenProductNotFound() {
            //given
            Long nonExistentProductId = 999L;

            //when & then
            assertThrows(CoreException.class, () -> {
                productService.increaseLikeCount(nonExistentProductId);
            });
        }
    }

    @DisplayName("좋아요 수 증가 기능을 테스트할 때")
    @Nested
    class DecreaseLikeCount {
        @DisplayName("상품이 존재하면 상품의 좋아요 수를 감소시킨다.")
        @Test
        void decreaseLikeCount_whenProductExists() {
            //given
            Product product =
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            20000L
                    ));
            product.increaseLikeCount(); // 초기 좋아요 수를 1로 설정
            product = productJpaRepository.save(product);

            //when
            productService.decreaseLikeCount(product.getId());

            //then
            Product findProduct = productJpaRepository.findById(product.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findProduct.getLikeCount()).isEqualTo(0L)
            );
        }

        @DisplayName("존재하는 상품의 좋아요 수가 0일 때 좋아요 수를 감소시키면 좋아요 수는 0으로 유지된다.")
        @Test
        void zeroLikeCount_whenProductLikeCountZero() {
            //given
            var product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L,
                    "루퍼스 공식 티셔츠",
                    "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                    "https://loopers.com/product/t-shirt.png",
                    20000L
            )));

            //when
            productService.decreaseLikeCount(product.getId());

            //then
            Product findProduct = productJpaRepository.findById(product.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findProduct.getLikeCount()).isEqualTo(0L)
            );
        }

        @DisplayName("존재하지 않는 상품의 좋아요 수를 증가시키면 예외가 발생한다.")
        @Test
        void throwException_whenProductNotFound() {
            //given
            Long nonExistentProductId = 999L;

            //when & then
            assertThrows(CoreException.class, () -> {
                productService.decreaseLikeCount(nonExistentProductId);
            });
        }
    }

    @DisplayName("상품 리스트를 조회할 때")
    @Nested
    class FindAllBy {
        @DisplayName("상품 ID 리스트가 주어지면 해당 상품들을 반환한다.")
        @Test
        void returnProducts_whenProductIdsAreProvided() {
            //given
            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            20000L
                    ))
            );
            var product2 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            2L,
                            "루퍼스 공식 후드티",
                            "루퍼스의 공식 후드티입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/hoodie.png",
                            30000L
                    ))
            );

            //when
            var products = productService.findAllBy(List.of(product1.getId(), product2.getId()));

            //then
            assertAll(
                    () -> assertThat(products).hasSize(2),
                    () -> assertThat(products).containsExactlyInAnyOrder(product1, product2)
            );
        }
    }
}
