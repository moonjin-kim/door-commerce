package com.loopers.interfaces.api.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
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
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointV1ApiE2ETest {
    private final UserJpaRepository userJpaRepository;
    private final PointJpaRepository pointJpaRepository;
    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public PointV1ApiE2ETest(
            UserJpaRepository userJpaRepository,
            PointJpaRepository pointJpaRepository,
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.userJpaRepository = userJpaRepository;
        this.pointJpaRepository = pointJpaRepository;
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class ChargePoint {
        private static final String ENDPOINT_CHARGE = "/api/v1/points/charge";

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsUserBalance_whenValidBodyIsProvided() {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );
            var request = new PointV1Request.PointChargeRequest(1000L);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(user.getId()));

            //when
            ParameterizedTypeReference<ApiResponse<PointV1Response.PointBalance>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointV1Response.PointBalance>> response =
                    testRestTemplate.exchange(
                            ENDPOINT_CHARGE,
                            HttpMethod.POST,
                            new HttpEntity<PointV1Request.PointChargeRequest>(request, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(1000)
            );
        }

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsUserBalance_whenValidBodyIsProvided2() {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Long amount = 10000L;
            Point point = pointJpaRepository.save(
                    Point.create(user.getId())
            );
            var request = new PointV1Request.PointChargeRequest(amount);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(user.getId()));

            //when
            ParameterizedTypeReference<ApiResponse<PointV1Response.PointBalance>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointV1Response.PointBalance>> response =
                    testRestTemplate.exchange(
                            ENDPOINT_CHARGE,
                            HttpMethod.POST,
                            new HttpEntity<PointV1Request.PointChargeRequest>(request, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(
                            point.getBalance().plus(
                                    new BigDecimal(amount)
                            ).longValue()
                    )
            );
        }

        @DisplayName("존재하는 않는 유저가 충전할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwsException_whenInvalidIdIsProvided() {
            //given
            var request = new PointV1Request.PointChargeRequest(1000L);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<PointV1Response.PointBalance>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointV1Response.PointBalance>> response =
                    testRestTemplate.exchange(
                            ENDPOINT_CHARGE,
                            HttpMethod.POST,
                            new HttpEntity<PointV1Request.PointChargeRequest>(request, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }


    @DisplayName("GET /api/v1/points")
    @Nested
    class GetBalance {
        private static final String ENDPOINT_GET_BALANCE = "/api/v1/points";

        @DisplayName("내 잔액 조회에 성공할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsUserBalance_whenValidUserIdIsProvided() {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            // 충전 이력을 생성
            Point point = Point.create(user.getId());
            point.charge(1000);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(user.getId()));

            //when
            ParameterizedTypeReference<ApiResponse<PointV1Response.PointBalance>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointV1Response.PointBalance>> response =
                    testRestTemplate.exchange(
                            ENDPOINT_GET_BALANCE,
                            HttpMethod.GET,
                            new HttpEntity<>(null, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(chargePoint.getBalance().longValue())
            );
        }

        @DisplayName("충전 이력이 없는 유저 잔액을 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsUserZeroBalance_whenValidUserIdIsProvidedAndNotChargedHistory() {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", String.valueOf(user.getId()));

            //when
            ParameterizedTypeReference<ApiResponse<PointV1Response.PointBalance>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointV1Response.PointBalance>> response =
                    testRestTemplate.exchange(
                            ENDPOINT_GET_BALANCE,
                            HttpMethod.GET,
                            new HttpEntity<>(null, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwsException_whenInvalidIdIsProvided() {
            //given
            var request = new PointV1Request.PointChargeRequest(1000L);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<PointV1Response.PointBalance>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointV1Response.PointBalance>> response =
                    testRestTemplate.exchange(
                            ENDPOINT_GET_BALANCE,
                            HttpMethod.GET,
                            new HttpEntity<>(request, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }
}
