package com.loopers.interfaces.api.order;

import com.loopers.domain.PageResponse;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointStatus;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.stock.StockJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Response;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderV1ControllerTest {
    @Autowired
    private final ProductJpaRepository productJpaRepository;
    @Autowired
    private final PointJpaRepository pointJpaRepository;
    @Autowired
    private final PointHistoryJpaRepository pointHistoryJpaRepository;
    @Autowired
    private final StockJpaRepository stockJpaRepository;
    @Autowired
    private final UserJpaRepository userJpaRepository;
    @Autowired
    private final OrderJpaRepository orderJpaRepository;
    @Autowired
    private final TestRestTemplate testRestTemplate;
    @Autowired
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public OrderV1ControllerTest(
            ProductJpaRepository productJpaRepository,
            PointJpaRepository pointJpaRepository,
            PointHistoryJpaRepository pointHistoryJpaRepository,
            StockJpaRepository stockJpaRepository,
            UserJpaRepository userJpaRepository,
            OrderJpaRepository orderJpaRepository,
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.productJpaRepository = productJpaRepository;
        this.pointJpaRepository = pointJpaRepository;
        this.pointHistoryJpaRepository = pointHistoryJpaRepository;
        this.stockJpaRepository = stockJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.testRestTemplate = testRestTemplate;
        this.orderJpaRepository = orderJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("Post /api/v1/orders - 주문 생성")
    @Nested
    class OrderControllerTest {
        private static final String URL = "/api/v1/orders";

        @DisplayName("주문 생성 성공")
        @Test
        void returnOrder_whenCurrenOrder(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.init(user.getId());
            point.charge(20000L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.init(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.POST,
                            new HttpEntity<OrderV1Request.Order>(request, headers),
                            responseType
                    );

            //then
            com.loopers.domain.order.Order order = orderJpaRepository.findAll().get(0);
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertNotNull(order),
                    () -> assertThat(response.getBody().data().id()).isNotNull(),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(user.getId()),
                    () -> assertThat(response.getBody().data().items()).hasSize(1),
                    () -> assertThat(response.getBody().data().items().get(0).productId()).isEqualTo(product1.getId()),
                    () -> assertThat(response.getBody().data().items().get(0).quantity()).isEqualTo(2),
                    () -> assertThat(response.getBody().data().totalPrice()).isEqualTo(20000L)
            );
        }

        @DisplayName("주문이 생성되었을 때, 주문 수량만큼 재고가 차감된다.")
        @Test
        void decreaseStock_whenSuccessOrder(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.init(user.getId());
            point.charge(20000L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.init(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.POST,
                            new HttpEntity<OrderV1Request.Order>(request, headers),
                            responseType
                    );

            //then
            Stock stock1 = stockJpaRepository.findByProductId(product1.getId()).get();
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(stock1.getQuantity()).isEqualTo(0)
            );
        }

        @DisplayName("주문이 생성되었을 때, 사용한 포인트만큼 포인트가 차감된다.")
        @Test
        void decreasePoint_whenSuccessOrder(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.init(user.getId());
            point.charge(20000L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.init(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.POST,
                            new HttpEntity<OrderV1Request.Order>(request, headers),
                            responseType
                    );

            //then
            Point point1 = pointJpaRepository.findAll().get(0);
            PointHistory pointHistory = pointHistoryJpaRepository.findAll().get(0);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(point1.balance().value()).isEqualTo(0)
                    ,
                    () -> assertThat(pointHistory.getPointId()).isEqualTo(point1.getId()),
                    () -> assertThat(pointHistory.getAmount()).isEqualTo(-20000L),
                    () -> assertThat(pointHistory.getStatus()).isEqualTo(PointStatus.USE),
                    () -> assertThat(pointHistory.getOrderId()).isEqualTo(response.getBody().data().id())
            );
        }

        @DisplayName("재고가 부족하면, 주문이 취소되고 400 Bad Request 응답을 반환한다.")
        @Test
        void returnBadRequest_when(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.init(user.getId());
            point.charge(20000L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.init(
                    StockCommand.Create.of(product1.getId(),1)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.POST,
                            new HttpEntity<OrderV1Request.Order>(request, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

        @DisplayName("포인트가 부족하면, 주문이 취소되고 400 Bad Request 응답을 반환한다.")
        @Test
        void returnBadRequest_whenLackPoint(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.init(user.getId());
            point.charge(19999L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.init(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.POST,
                            new HttpEntity<OrderV1Request.Order>(request, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

        @DisplayName("주문 상품에 존재하지 않는 상품이 존재하면, 주문이 취소되고 404 Bad Request 응답을 반환한다.")
        @Test
        void returnNotFound_whenProductIsNo(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.init(user.getId());
            point.charge(19999L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.init(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2),new OrderV1Request.OrderItem(2L, 2))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.POST,
                            new HttpEntity<OrderV1Request.Order>(request, headers),
                            responseType
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    @DisplayName("Get /api/v1/orders/{orderId} - 주문 조회")
    @Nested
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    class GetBy {
        private static final String URL = "/api/v1/orders/{orderId}";

        @DisplayName("주문 조회 성공")
        @Test
        void returnOrder_whenSuccessGetBy() {
            //given
            var order = orderJpaRepository.save(Order.order(
                    OrderCommand.Order.of(
                            1L,
                            List.of(
                                    new OrderCommand.OrderItem(2L, "루퍼스 공식 티셔츠", 10000L, 2)
                            )
                    )
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            responseType,
                            order.getId()
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(order.getId()),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(1L),
                    () -> assertThat(response.getBody().data().items()).hasSize(1),
                    () -> assertThat(response.getBody().data().items().get(0).productId()).isEqualTo(2L),
                    () -> assertThat(response.getBody().data().items().get(0).quantity()).isEqualTo(2),
                    () -> assertThat(response.getBody().data().totalPrice()).isEqualTo(20000L)
            );
        }

        @DisplayName("주문 조회 시 사용자 ID가 일치하지 않으면, 403 FORBIDDEN 에러를 발생시킨다.")
        @Test
        void throw403_whenNotPermissionByUser() {
            //given
            var order = orderJpaRepository.save(Order.order(
                    OrderCommand.Order.of(
                            1L,
                            List.of(
                                    new OrderCommand.OrderItem(1L, "루퍼스 공식 티셔츠", 10000L, 2)
                            )
                    )
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "10");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            responseType,
                            order.getId()
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN)
            );
        }

        @DisplayName("주문한 상품이 존재하지 않으면, 404 NOT_FOUND에러를 발상핸다.")
        @Test
        void returnNotFound_whenOrderIdIsNotExist(){
            //given
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "1");

            //when
            ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                    testRestTemplate.exchange(
                            URL,
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            responseType,
                            10L
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    @DisplayName("Get /api/v1/orders - 주문 목로 조회")
    @Nested
    class GetOrdersBy {
        private static final String URL = "/api/v1/orders";

        @DisplayName("주문 조회 성공")
        @Test
        void returnOrder_whenSuccessGetBy() {
            //given
            var order = orderJpaRepository.save(Order.order(
                    OrderCommand.Order.of(
                            2L,
                            List.of(
                                    new OrderCommand.OrderItem(1L, "루퍼스 공식 티셔츠", 10000L, 2)
                            )
                    )
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", "2");

            //when
            ParameterizedTypeReference<ApiResponse<PageResponse<OrderV1Response.Order>>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PageResponse<OrderV1Response.Order>>> response =
                    testRestTemplate.exchange(
                            URL + "?page=1&size=10",
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            responseType,
                            order.getId()
                    );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertThat(response.getBody().data().getTotalCount()).isEqualTo(1),
                    () -> assertThat(response.getBody().data().getPage()).isEqualTo(1),
                    () -> assertThat(response.getBody().data().getSize()).isEqualTo(10),
                    () -> assertThat(response.getBody().data().getItems()).hasSize(1),
                    () -> assertThat(response.getBody().data().getItems().get(0).id()).isEqualTo(order.getId())
            );
        }
    }
}
