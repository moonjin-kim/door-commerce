package com.loopers.application.order;

import com.loopers.application.order.coupon.CouponApplier;
import com.loopers.application.order.coupon.CouponApplierCommand;
import com.loopers.application.order.coupon.CouponApplierInfo;
import com.loopers.application.order.payment.PaymentCriteria;
import com.loopers.application.order.payment.PaymentProcess;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.pg.PgService;
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
    public Order prepareOrder(OrderCriteria.Order criteria) {
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

            order.applyCoupon(
                    discountInfo.userCouponId(),
                    discountInfo.discountAmount()
            );
        }

        // 재고 차감
        orderItems.forEach(orderItem -> {
            stockService.decrease(StockCommand.Decrease.from(orderItem));
        });

        return order;
    }
}
