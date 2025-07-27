package com.loopers.interfaces.api.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.brand.BrandV1Response;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductV1E2ETest {
    @Autowired
    private final ProductJpaRepository productJpaRepository;
    @Autowired
    private final TestRestTemplate testRestTemplate;
    @Autowired
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public ProductV1E2ETest(ProductJpaRepository productJpaRepository, TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp) {
        this.productJpaRepository = productJpaRepository;
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("Get /api/v1/products/{productId}")
    @Nested
    class GetProduct {
        private static final Function<Long, String> ENDPOINT_GET = id -> "/api/v1/products/" + id;

        @DisplayName("존재하지 않는 상품ID로 요청 시, 404 Not_Found 예외를 바는다.")
        @Test
        void throwNotFound_whenProductIdIsNoExist() {
            //given
            String requestUrl = ENDPOINT_GET.apply(1L);

            //when
            var response = testRestTemplate.getForEntity(requestUrl, String.class);

            //then
            assertAll(
                () -> assertEquals(404, response.getStatusCodeValue()),
                () -> assertTrue(response.getBody().contains("Not Found"))
            );
        }

        @DisplayName("상품 아이디로 상품 정보를 요청시, 상품 정보를 받는다.")
        @Test
        void getBrandResponse_whenBrandIdIsProvide(){
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
            String requestUrl = ENDPOINT_GET.apply(1L);

            //when
            ParameterizedTypeReference<ApiResponse<ProductV1Response.ProductDetail>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<ProductV1Response.ProductDetail>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.GET,
                            new HttpEntity<ProductV1Response.ProductDetail>(null, null),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().productId()).isEqualTo(1L),
                    () -> assertThat(response.getBody().data().name()).isEqualTo("루퍼스 공식 티셔츠"),
                    () -> assertThat(response.getBody().data().description()).isEqualTo("루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다."),
                    () -> assertThat(response.getBody().data().imageUrl()).isEqualTo("https://loopers.com/product/t-shirt.png"),
                    () -> assertThat(response.getBody().data().price()).isEqualTo(20000L)
            );
        }

    }
}
