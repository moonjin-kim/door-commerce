package com.loopers.application.order;

import com.loopers.application.payment.PaymentProcess;
import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final StockService stockService;
    private final UserService userService;
    private final CouponService couponService;
    private final PaymentProcess paymentProcess;

    @Transactional
    public OrderResult.Order order(OrderCriteria.Order criteria) {
        // Validate user
        var user = userService.getUser(criteria.userId());
        if (user.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "User not found: " + criteria.userId());
        }

        //주문 상품 조회
        List<Long> productIds = criteria.items().stream()
                .map(OrderCriteria.OrderItem::productId)
                .toList();
        List<ProductInfo> productInfos = productService.findAllBy(productIds);
        Map<Long, ProductInfo> productMap = productInfos.stream()
                .collect(Collectors.toMap(ProductInfo::id, productInfo -> productInfo));

        // 주문 아이템 생성
        List<OrderCommand.OrderItem> orderItems = criteria.items().stream().map(
                item -> {
                    ProductInfo product = productMap.get(item.productId());
                    if (product == null) {
                        throw new CoreException(ErrorType.NOT_FOUND, "Product not found: " + item.productId());
                    }
                    return new OrderCommand.OrderItem(
                            item.productId(),
                            product.name(),
                            product.price(),
                            item.quantity()
                    );
                }
        ).toList();

        //주문서 생성
        Order order = orderService.order(OrderCommand.Order.of(criteria.userId(), orderItems));

        if(criteria.couponId() != null) {
            // 쿠폰 사용
            UserCoupon userCoupon = couponService.getUserCoupon(
                    CouponCommand.Get.of(criteria.userId(), criteria.couponId())
            );
            order.applyCoupon(userCoupon.getId(), userCoupon.getDiscountPolicy());

            // 쿠폰 사용 기록 추가
            userCoupon.use(order.getId(), LocalDateTime.now());
        }

        // 포인트 사용
        paymentProcess.processPayment(
            PaymentCommand.Pay.of(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount().longValue(),
                "pointPayment"
            )
        );

        // 재고 차감
        List<StockCommand.Decrease> stockCommands = orderItems.stream()
                .map(item -> StockCommand.Decrease.of(item.productId(), item.quantity()))
                .toList();
        stockService.decreaseAll(stockCommands);

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
