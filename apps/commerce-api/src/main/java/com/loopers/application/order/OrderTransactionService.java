package com.loopers.application.order;

import com.loopers.application.order.coupon.CouponApplier;
import com.loopers.application.order.coupon.CouponApplierCommand;
import com.loopers.application.order.coupon.CouponApplierInfo;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
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

@Component
@RequiredArgsConstructor
public class OrderTransactionService {
    private final OrderService orderService;
    private final ProductService productService;
    private final StockService stockService;
    private final UserService userService;
    private final CouponApplier couponApplier;

    @Transactional
    public OrderResult.Order prepareOrder(OrderCriteria.Order criteria) {
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
            stockService.decrease(StockCommand.Decrease.from(orderItem));
        });

        return OrderResult.Order.from(order);
    }

    @Transactional
    public OrderResult.Order cancelOrder(String orderId) {
        // 주문 조회
        Order order = orderService.getByOrderId(orderId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문: " + orderId)
        );

        // 재고 증가
        order.getOrderItems().forEach(orderItem -> {
            stockService.increase(StockCommand.Increase.of(orderItem.getProductId(), orderItem.getQuantity()));
        });

        order = orderService.cancel(orderId);

        // 주문 취소
        return OrderResult.Order.from(order);
    }

    @Transactional
    public OrderResult.Order complete(String orderId) {
        Order order = orderService.complete(orderId);
        return OrderResult.Order.from(order);
    }
}
