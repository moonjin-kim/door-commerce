package com.loopers.application.order;

import com.loopers.application.order.coupon.CouponProcessor;
import com.loopers.application.order.coupon.CouponApplierCommand;
import com.loopers.application.order.coupon.CouponApplierInfo;
import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserService;
import com.loopers.domain.order.OrderEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final StockService stockService;
    private final OrderEventPublisher orderEventPublisher;
    private final ProductService productService;
    private final UserService userService;
    private final CouponProcessor couponApplier;

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
                    return OrderCommand.OrderItem.from(
                            product,
                            item.quantity()
                    );
                })
                .toList();

        //주문서 생성
        Order order = orderService.create(OrderCommand.Order.of(criteria.userId(), orderItems));

        // 쿠폰 적용
        if(criteria.couponId() != null) {
            CouponApplierInfo.ApplyCoupon discountInfo = couponApplier.applyCoupon(
                    new CouponApplierCommand.Apply(
                            criteria.userId(),
                            criteria.couponId(),
                            order.getId(),
                            order.getTotalAmount().value()
                    )
            );

            order.applyCoupon(
                    discountInfo.userCouponId(),
                    discountInfo.discountAmount()
            );
        }

        // 재고 차감
        orderItems.forEach(orderItem -> {
            orderEventPublisher.publish(
                    OrderEvent.ConsumeStockCommand.of(orderItem.productId(), orderItem.quantity())
            );
        });

        // 결제
        orderEventPublisher.publish(
            OrderEvent.RequestPayment.of(
                order.getOrderId(),
                order.getUserId(),
                order.getFinalAmount().longValue(),
                criteria.paymentMethodType(),
                criteria.cardType(),
                criteria.cardNumber()
            )
        );

        return OrderResult.Order.from(order);
    }

    @Transactional
    public void cancelOrder(String orderId) {
        Order order = orderService.cancel(orderId);

        // 재고 증가
        order.getOrderItems().forEach(orderItem -> {
            orderEventPublisher.publish(
                    OrderEvent.RollbackStockCommand.of(orderItem.getProductId()
                            , orderItem.getQuantity())
            );
        });

        couponApplier.cancelCoupon(order);

        orderEventPublisher.publish(OrderEvent.Cancel.of(orderId, order.getFinalAmount().longValue()));
    }

    @Transactional
    public void completeOrder(String orderId) {
        // 주문 완료
        Order order = orderService.complete(orderId);

        orderEventPublisher.publish(OrderEvent.Complete.of(orderId, order.getFinalAmount().longValue()));
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
