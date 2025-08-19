package com.loopers.application.order;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentProcess;
import com.loopers.application.payment.pg.PgProcess;
import com.loopers.application.payment.pg.PgResult;
import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final StockService stockService;
    private final UserService userService;
    private final PaymentProcess paymentProcess;
    private final CouponApplier couponApplier;
    private final PgProcess pgProcess;

    @Transactional
    public OrderResult.Order order(OrderCriteria.Order criteria) {
        // Validate user
        var user = userService.getUser(criteria.userId());
        if (user.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "User not found: " + criteria.userId());
        }

        //주문 상품 조회
        List<OrderCommand.OrderItem> orderItems = criteria.items().stream()
                .map(item -> {
                    Product product = productService.getBy(item.productId()).orElseThrow(
                            () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품 : " + item.productId()
                    ));
                    System.out.println("상품 정보: " + product.getBrandId() + ", 가격: " + product.getPrice().value());
                    return OrderCommand.OrderItem.from(
                            product,
                            item.quantity()
                    );
                })
                .toList();

        //주문서 생성
        Order order = orderService.order(OrderCommand.Order.of(criteria.userId(), orderItems));

        // 쿠폰 적용
        if(criteria.couponId() != null) {
            CouponApplierInfo.ApplyCoupon discountInfo = couponApplier.applyCoupon(
                    new CouponApplierCommand.ApplyCoupon(
                        criteria.userId(),
                        criteria.couponId(),
                        order.getId(),
                        order.getTotalAmount().value()
                    )
            );

            order.applyCoupon(discountInfo.userCouponId(), discountInfo.discountAmount());
        }

        // 결제
        paymentProcess.processPayment(
            PaymentCriteria.Pay.of(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount().longValue(),
                criteria.paymentMethodType().name(),
                criteria.cardType(),
                criteria.cardNumber()
            )
        );

        // 재고 차감
        orderItems.forEach(orderItem -> {
            stockService.decrease(StockCommand.Decrease.from(orderItem));
        });

        return OrderResult.Order.from(order);
    }

    @Transactional()
    public OrderResult.Order callback(OrderCriteria.Callback criteria) {
        // 주문 조회
        Order order = orderService.getByOrderId(criteria.orderId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문: " + criteria.orderId() )
        );

        PgResult.Find pgResult = pgProcess.findByPGId(criteria.transactionKey(), order.getUserId());
        if(pgResult == null) {
            orderService.cancel(criteria.orderId());
            paymentService.paymentFail(order.getOrderId(), "결제 조회 실패");

            order.getOrderItems().forEach(orderItem -> {
                stockService.increase(StockCommand.Increase.of(orderItem.getProductId(), orderItem.getQuantity()));
            });

            throw new CoreException(ErrorType.PAYMENT_ERROR, "결제 정보 조회 실패: " + criteria.orderId());
        }
        if(!Objects.equals(pgResult.status(), "SUCCESS")) {
            orderService.cancel(criteria.orderId());
            paymentService.paymentFail(order.getOrderId(), pgResult.reason());
            order.getOrderItems().forEach(orderItem -> {
                stockService.increase(StockCommand.Increase.of(orderItem.getProductId(), orderItem.getQuantity()));
            });

            return OrderResult.Order.from(order);
        }


        // 결제 완료 저장
        orderService.complete(criteria.orderId());
        paymentService.paymentComplete(order.getOrderId());

        return OrderResult.Order.from(order);
    }

    @Transactional(readOnly = true)
    public OrderResult.Order getBy(Long orderId, Long userId) {
        // 주문 정보 조회
        Order order =  orderService.getBy(OrderCommand.GetBy.of(orderId, userId))
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "[id = " + orderId + "] 존재하지 않는 주문입니다."));

        order.checkPermission(userId);

        // 주문 결과 반환
        return OrderResult.Order.from(order);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResult.Order> getOrders(PageRequest<OrderCriteria.GetOrdersBy> criteria) {
        // 주문 목록 조회
        PageResponse<Order> orderPage = orderService.getOrders(criteria.map(OrderCriteria.GetOrdersBy::toCommand));

        // 페이지 응답 생성
        return orderPage.map(OrderResult.Order::from);
    }
}
