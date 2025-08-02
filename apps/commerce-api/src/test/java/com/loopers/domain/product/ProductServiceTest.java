package com.loopers.domain.product;

import com.loopers.application.product.ProductResult;
import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.point.PointInfo;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
    private LikeJpaRepository likeJpaRepository;
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
        @DisplayName("존재하지 않는 상품 아이디가 주어지면 NotFound 예외가 발생한다.")
        @Test
        void returnEmpty_whenProductIsNotFound(){
            //given
            Long productId = 1L;

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                productService.getBy(productId);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
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
            ProductInfo foundProduct = productService.getBy(productId);

            //then
            assertAll(
                    () -> assertEquals(ProductInfo.of(product), foundProduct)
            );
        }
    }

    @DisplayName("상품을 검색할 때")
    @Nested
    class GetOrdersBy {
        @DisplayName("좋아요순 정렬 조건이 주어지면 해당 조건에 맞는 상품 페이지를 반환한다.")
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
            likeJpaRepository.saveAll(List.of(
                    Like.create(new LikeCommand.Like(1L, product1.getId())),
                    Like.create(new LikeCommand.Like(2L, product1.getId())),
                    Like.create(new LikeCommand.Like(1L, product2.getId()))
            ));

            PageRequest<ProductCommand.Search> query = PageRequest.of(1, 10, ProductCommand.Search.of("like_desc", null));

            //when
            PageResponse<ProductView> productPage = productService.search(query);

            //then
            assertAll(
                    () -> assertThat(productPage.getPage()).isEqualTo(1),
                    () -> assertThat(productPage.getSize()).isEqualTo(10),
                    () -> assertThat(productPage.getTotalCount()).isEqualTo(2),
                    () -> assertThat(productPage.getItems()).hasSize(2),
                    () -> assertThat(productPage.getItems().get(0).getId()).isEqualTo(product1.getId()),
                    () -> assertThat(productPage.getItems().get(0).getLikeCount()).isEqualTo(2),
                    () -> assertThat(productPage.getItems().get(1).getId()).isEqualTo(product2.getId())
            );
        }

        @DisplayName("검색 조건이 주어지면 해당 조건에 맞는 상품 페이지를 반환한다.")
        @Test
        void returnSortLikeCount_whenSearchQueryIsProvided() {
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


            PageRequest<ProductCommand.Search> query = PageRequest.of(1, 10, ProductCommand.Search.of(null, null));

            //when
            PageResponse<ProductView> productPage = productService.search(query);

            //then
            assertAll(
                    () -> assertThat(productPage.getPage()).isEqualTo(1),
                    () -> assertThat(productPage.getSize()).isEqualTo(10),
                    () -> assertThat(productPage.getTotalCount()).isEqualTo(2),
                    () -> assertThat(productPage.getItems()).hasSize(2)
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

            PageRequest<ProductCommand.Search> query = PageRequest.of(1, 10, ProductCommand.Search.of(null, 1L));

            //when
            PageResponse<ProductView> productPage = productService.search(query);

            //then
            assertAll(
                    () -> assertThat(productPage.getPage()).isEqualTo(1),
                    () -> assertThat(productPage.getSize()).isEqualTo(10),
                    () -> assertThat(productPage.getItems()).hasSize(1),
                    () -> assertThat(productPage.getItems().get(0).getId()).isEqualTo(product1.getId())
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

            PageRequest<ProductCommand.Search> query = PageRequest.of(1, 10, ProductCommand.Search.of("price_asc", null));

            //when
            PageResponse<ProductView> productPage = productService.search(query);

            //then
            assertAll(
                    () -> assertThat(productPage.getPage()).isEqualTo(1),
                    () -> assertThat(productPage.getSize()).isEqualTo(10),
                    () -> assertThat(productPage.getItems()).hasSize(2),
                    () -> assertThat(productPage.getItems().get(0).getId()).isEqualTo(
                            product2.getId())
            );
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
            List<ProductInfo> products = productService.findAllBy(List.of(product1.getId(), product2.getId()));

            //then
            assertAll(
                    () -> assertThat(products).hasSize(2),
                    () -> assertThat(products).containsExactlyInAnyOrder(ProductInfo.of(product1), ProductInfo.of(product2))
            );
        }
    }
}
