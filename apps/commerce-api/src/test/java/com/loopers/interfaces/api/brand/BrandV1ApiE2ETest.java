package com.loopers.interfaces.api.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandV1ApiE2ETest {
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private final TestRestTemplate testRestTemplate;
    @Autowired
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public BrandV1ApiE2ETest(BrandJpaRepository brandJpaRepository, TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp) {
        this.brandJpaRepository = brandJpaRepository;
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("Get /api/v1/brands/{brandId}")
    @Nested
    class GetBrand {
        private static final Function<Long, String> ENDPOINT_GET = id -> "/api/v1/brands/" + id;

        @DisplayName("존재하지 않는 브랜드 ID로 브랜드 요청시, 404 Not_Found 예외를 바는다.")
        @Test
        void throwNotFound_whenBrandIdIsNoExist(){
            //given
            String requestUrl = ENDPOINT_GET.apply(1L);

            //when
            ParameterizedTypeReference<ApiResponse<BrandV1Response.Brand>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<BrandV1Response.Brand>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.GET,
                            new HttpEntity<BrandV1Response.Brand>(null, null),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

        @DisplayName("브랜드 아이디로 브랜드 정보를 요청시, 브랜드 정보를 받는다.")
        @Test
        void getBrandResponse_whenBrandIdIsProvide(){
            //given
            var brand = brandJpaRepository.save(
                    Brand.create(BrandCommand.Create.of(
                            "루퍼스",
                            "루퍼스는 루퍼스입니다.",
                            "https://loopers.com/logo.png"
                    ))
            );
            String requestUrl = ENDPOINT_GET.apply(brand.getId());

            //when
            ParameterizedTypeReference<ApiResponse<BrandV1Response.Brand>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<BrandV1Response.Brand>> response =
                    testRestTemplate.exchange(
                            requestUrl,
                            HttpMethod.GET,
                            new HttpEntity<BrandV1Response.Brand>(null, null),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().brandId()).isEqualTo(brand.getId()),
                    () -> assertThat(response.getBody().data().name()).isEqualTo(brand.getName()),
                    () -> assertThat(response.getBody().data().description()).isEqualTo(brand.getDescription()),
                    () -> assertThat(response.getBody().data().logoUrl()).isEqualTo(brand.getLogoUrl())
            );
        }
    }
}
