package com.loopers.interfaces.api.order;

import com.loopers.application.order.payment.PaymentMethodType;
import com.loopers.domain.PageResponse;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponJpaRepository;
import com.loopers.domain.coupon.DiscountType;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderV1ControllerTest {
    @Autowired
    private final CouponJpaRepository couponJpaRepository;
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
    private RedisCleanUp redisCleanUp;

    @Autowired
    public OrderV1ControllerTest(
            CouponJpaRepository couponRepository,
            ProductJpaRepository productJpaRepository,
            PointJpaRepository pointJpaRepository,
            PointHistoryJpaRepository pointHistoryJpaRepository,
            StockJpaRepository stockJpaRepository,
            UserJpaRepository userJpaRepository,
            OrderJpaRepository orderJpaRepository,
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.couponJpaRepository = couponRepository;
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
        redisCleanUp.truncateAll();
    }

    @DisplayName("Post /api/v1/orders - 주문 생성")
    @Nested
    class OrderControllerTest {
        private static final String URL = "/api/v1/orders";

        @DisplayName("주문 생성 성공")
        @Test
        void returnOrder_whenCurrenCreate(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
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

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
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
            Order order = orderJpaRepository.findAll().get(0);
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
        void decreaseStock_whenSuccessCreate(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
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

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
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
        void decreasePoint_whenSuccessCreate(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
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

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
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
                    () -> assertThat(point1.balance().longValue()).isEqualTo(0L)
                    ,
                    () -> assertThat(pointHistory.getPointId()).isEqualTo(point1.getId()),
                    () -> assertThat(pointHistory.getAmount()).isEqualTo(-20000L),
                    () -> assertThat(pointHistory.getStatus()).isEqualTo(PointStatus.USE),
                    () -> assertThat(pointHistory.getOrderId()).isEqualTo(response.getBody().data().orderId())
            );
        }

        @DisplayName("재고가 부족하면, 주문이 취소되고 400 Bad Request 응답을 반환한다.")
        @Test
        void returnBadRequest_when(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
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

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),1)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null

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

            Point point = Point.create(user.getId());
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

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2)), null,
                    PaymentMethodType.POINT,
                    null,
                    null
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

            Point point = Point.create(user.getId());
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

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2),new OrderV1Request.OrderItem(2L, 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
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

        @DisplayName("쿠폰이 주어지면, 쿠폰이 적용되어 할인된 주문을 생성한다.")
        @Test
        void applyCoupon_whenRequestCoupon(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
            point.charge(20000L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(
                            CouponCommand.Create.of(
                                    "10% 할인 쿠폰",
                                    "10% 할인 쿠폰입니다.",
                                    BigDecimal.valueOf(10),
                                    DiscountType.PERCENT
                            )
                    )
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2)),
                    coupon.getId(),
                    PaymentMethodType.POINT,
                    null,
                    null
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
            Order order = orderJpaRepository.findAll().get(0);
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertNotNull(order),
                    () -> assertThat(response.getBody().data().id()).isNotNull(),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(user.getId()),
                    () -> assertThat(response.getBody().data().items()).hasSize(1),
                    () -> assertThat(response.getBody().data().items().get(0).productId()).isEqualTo(product1.getId()),
                    () -> assertThat(response.getBody().data().items().get(0).quantity()).isEqualTo(2),
                    () -> assertThat(response.getBody().data().totalPrice()).isEqualTo(20000L),
                    () -> assertThat(response.getBody().data().userCouponId()).isEqualTo(coupon.getId()),
                    () -> assertThat(response.getBody().data().couponDiscount()).isEqualTo(2000L),
                    () -> assertThat(response.getBody().data().finalAmount()).isEqualTo(18000L)
            );
        }

        @DisplayName("존재하지 않은 쿠폰이 주어지면 404 NOT_FOUND 에러를 발생시킨다.")
        @Test
        void throwNotFound_whenNotExistCoupon(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
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

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(),2)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product1.getId(), 2)),
                    1L,
                    PaymentMethodType.POINT,
                    null,
                    null
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




    @DisplayName("Get /api/v1/orders/{orderId} - 주문 조회 주문 조회 성공")
    @Test
    void returnOrder_whenSuccessGetBy() {
        //given
        List<OrderCommand.OrderItem> orderItems = List.of(new OrderCommand.OrderItem(2L, "루퍼스 공식 티셔츠", 10000L, 2));
        Order order2 = Order.create(
                OrderCommand.Order.of(
                        1L,
                        orderItems
                )
        );
        Order saveOrder = orderJpaRepository.save(order2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-USER-ID", "1");

        //when
        ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<ApiResponse<OrderV1Response.Order>> response =
                testRestTemplate.exchange(
                        "/api/v1/orders/" + saveOrder.getId(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        responseType
                );

        //then
        assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertNotNull(response.getBody()),
                () -> assertThat(response.getBody().data().id()).isEqualTo(saveOrder.getId()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo(1L),
                () -> assertThat(response.getBody().data().items()).hasSize(1),
                () -> assertThat(response.getBody().data().items().get(0).productId()).isEqualTo(2L),
                () -> assertThat(response.getBody().data().items().get(0).quantity()).isEqualTo(2),
                () -> assertThat(response.getBody().data().totalPrice()).isEqualTo(20000L)
        );
    }

    @DisplayName("Get /api/v1/orders/{orderId} - 주문 조회")
    @Nested
    class GetBy {
        private static final String URL = "/api/v1/orders/";


        @DisplayName("주문 조회 시 사용자 ID가 일치하지 않으면, 403 FORBIDDEN 에러를 발생시킨다.")
        @Test
        void throw403_whenNotPermissionByUser() {
            //given
            Order order = orderJpaRepository.save(Order.create(
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
                            URL + order.getId(),
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
        @Transactional
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

    @DisplayName("Get /api/v1/orders - 주문 목록 조회")
    @Nested
    class GetOrdersBy {
        private static final String URL = "/api/v1/orders";

        @DisplayName("주문 조회 성공")
        @Test
        void returnCreate_whenSuccessGetBy() {
            //given
            var order = orderJpaRepository.save(Order.create(
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
                    () -> assertThat(response.getBody().data().getPage()).isEqualTo(1),
                    () -> assertThat(response.getBody().data().getSize()).isEqualTo(10),
                    () -> assertThat(response.getBody().data().getItems()).hasSize(1),
                    () -> assertThat(response.getBody().data().getItems().get(0).id()).isEqualTo(order.getId())
            );
        }

        @DisplayName("동시에 같은 유저의 같은 주문건이 요청되어도, 포인트는 정상적으로 차감된다.")
        @Test
        void notAllowNegativeStock_whenConcurrentCreate() throws InterruptedException {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
            point.charge(100000L);
            pointJpaRepository.save(point);

            var product = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    ))
            );

            stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product.getId(), 10)
            ));

            var request = new OrderV1Request.Order(
                    List.of(new OrderV1Request.OrderItem(product.getId(), 1)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", user.getId().toString());

            int threadCount = 10;
            Thread[] threads = new Thread[threadCount];
            ResponseEntity<ApiResponse<OrderV1Response.Order>>[] responses = new ResponseEntity[threadCount];

            for (int i = 0; i < threadCount; i++) {
                final int idx = i;
                threads[i] = new Thread(() -> {
                    ParameterizedTypeReference<ApiResponse<OrderV1Response.Order>> responseType = new ParameterizedTypeReference<>() {};
                    responses[idx] = testRestTemplate.exchange(
                            "/api/v1/orders",
                            HttpMethod.POST,
                            new HttpEntity<>(request, headers),
                            responseType
                    );
                });
            }

            //when
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }

            //then
            long successCount = 0;
            long failCount = 0;
            for (ResponseEntity<ApiResponse<OrderV1Response.Order>> response : responses) {
                if (response != null && response.getStatusCode().is2xxSuccessful()) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            Point point1 = pointJpaRepository.findByUserId(user.getId()).get();
            assertAll(
                    () -> assertThat(point1.getBalance().longValue()).isEqualTo(0L) // 10 * 10000
            );
        }
    }
}
