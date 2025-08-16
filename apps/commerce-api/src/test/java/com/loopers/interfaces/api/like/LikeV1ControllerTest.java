package com.loopers.interfaces.api.like;

import com.loopers.domain.PageResponse;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LikeV1ControllerTest {
    @Autowired
    private final ProductJpaRepository productJpaRepository;
    @Autowired
    private final LikeJpaRepository likeJpaRepository;
    @Autowired
    private final TestRestTemplate testRestTemplate;
    @Autowired
    private final DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @Autowired
    public LikeV1ControllerTest(
            ProductJpaRepository productJpaRepository,
            LikeJpaRepository likeJpaRepository,
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp,
            RedisCleanUp redisCleanUp
    ) {
        this.productJpaRepository = productJpaRepository;
        this.likeJpaRepository = likeJpaRepository;
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.redisCleanUp = redisCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @DisplayName("Post /api/v1/like/products/{productId} - 좋아요")
    @Nested
    class like {
        private static final Function<Long, String> ENDPOINT_POST = (productId) -> "/api/v1/like/products/" + productId;

        @DisplayName("상품 아이디로 좋아요를 누르면, 성공 메시지를 반환하고 좋아요가 추가된다.")
        @Test
        void getSuccessMessage_whenProductIdIsProvider(){
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
            String requestUrl = ENDPOINT_POST.apply(product.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(1L));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.POST,
                            new HttpEntity<String>(null, headers),
                            responseType
                    );

            //then
            boolean like = likeJpaRepository.existsByUserIdAndProductId(1L, product.getId());
            assertAll(
                    () -> assertTrue(like),
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertEquals("좋아요에 성공했습니다", response.getBody().data())
            );
        }

        @DisplayName("좋아요가 이미 존재하도, 좋아요 성공을 반환한다.")
        @Test
        void returnOk_whenAlreadyLike(){
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
            likeJpaRepository.save(
                    Like.create(
                            LikeCommand.Like.of(1L, product.getId())
                    )
            );
            String requestUrl = ENDPOINT_POST.apply(1L);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(1L));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.POST,
                            new HttpEntity<String>(null, headers),
                            responseType
                    );

            //then
            boolean like = likeJpaRepository.existsByUserIdAndProductId(1L, product.getId());
            assertAll(
                    () -> assertTrue(like),
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertEquals("좋아요에 성공했습니다", response.getBody().data())
            );
        }

        @DisplayName("존재하지 않는 상품 아이디로 좋아요를 누르면, 404 NOT_FOUND 응답을 반환한다.")
        @Test
        void return404NotFound_whenNotExistProduct(){
            //given
            String requestUrl = ENDPOINT_POST.apply(1L);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(1L));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.POST,
                            new HttpEntity<String>(null, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    @DisplayName("Delete /api/v1/like/products/{productId} - 좋아요")
    @Nested
    class UnLike {
        private static final Function<Long, String> ENDPOINT_POST = (productId) -> "/api/v1/like/products/" + productId;

        @DisplayName("좋아요하지 않은 상품을 좋아여 취소를 누르면, 성공 메시지를 반환한다.")
        @Test
        void getSuccessMessage_whenProductIdIsProvider(){
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
            String requestUrl = ENDPOINT_POST.apply(product.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(1L));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.DELETE,
                            new HttpEntity<String>(null, headers),
                            responseType
                    );

            //then
            boolean like = likeJpaRepository.existsByUserIdAndProductId(1L, product.getId());
            assertAll(
                    () -> assertFalse(like),
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertEquals("좋아요에 성공했습니다", response.getBody().data())
            );
        }

        @DisplayName("상품 ID로 좋아요 취소를 누르면, 성공메시지를 반환하고, 좋아요가 삭제된다.")
        @Test
        void returnOk_whenAlreadyLike(){
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
            likeJpaRepository.save(
                    Like.create(
                            LikeCommand.Like.of(1L, product.getId())
                    )
            );
            String requestUrl = ENDPOINT_POST.apply(1L);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(1L));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.DELETE,
                            new HttpEntity<String>(null, headers),
                            responseType
                    );

            //then
            boolean like = likeJpaRepository.existsByUserIdAndProductId(1L, product.getId());
            assertAll(
                    () -> assertFalse(like),
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertEquals("좋아요에 성공했습니다", response.getBody().data())
            );
        }

        @DisplayName("존재하지 않는 상품 아이디로 좋아요 취소를 누르면, 404 NOT_FOUND 응답을 반환한다.")
        @Test
        void return404NotFound_whenNotExistProduct(){
            //given
            String requestUrl = ENDPOINT_POST.apply(1L);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(1L));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.DELETE,
                            new HttpEntity<String>(null, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    @DisplayName("Get /api/v1/like/products - 좋아요 상품 조회")
    @Nested
    class findAllBy {
        private static final String ENDPOINT_GET = "/api/v1/like/products";

        @DisplayName("좋아요 상품을 조회하면, 좋아요 상품 목록을 반환한다.")
        @Test
        void returnLikeProducts_whenUserIdIsProvided() {
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
            likeJpaRepository.save(
                    Like.create(
                            LikeCommand.Like.of(1L, product.getId())
                    )
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(1L));

            //when
            ParameterizedTypeReference<ApiResponse<PageResponse<LikeV1Response.LikeProduct>>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PageResponse<LikeV1Response.LikeProduct>>> response =
                    testRestTemplate.exchange(
                            ENDPOINT_GET + "?page=1&size=10",
                            HttpMethod.GET,
                            new HttpEntity<String>(null, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertEquals(1, response.getBody().data().getItems().size()),
                    () -> assertEquals(product.getId(), response.getBody().data().getItems().get(0).productId())
            );
        }
    }
}
