package com.loopers.application.order;

import com.loopers.application.order.coupon.CouponApplier;
import com.loopers.application.order.coupon.CouponApplierCommand;
import com.loopers.application.order.coupon.CouponApplierInfo;
import com.loopers.application.order.payment.PaymentCriteria;
import com.loopers.application.order.payment.PaymentProcess;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.pg.PgService;
import com.loopers.infrastructure.pg.PgResult;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final StockService stockService;
    private final PaymentProcess paymentProcess;
    private final OrderTransactionService orderTransactionService;
    private final PgService pgService;

    @Transactional
    public OrderResult.Order order(OrderCriteria.Order criteria) {
        // Validate user
        Order order = orderTransactionService.prepareOrder(criteria);

        // 결제
        PaymentInfo.Pay paymentInfo = paymentProcess.processPayment(
            PaymentCriteria.Pay.of(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount().longValue(),
                criteria.paymentMethodType().name(),
                criteria.cardType(),
                criteria.cardNumber()
            )
        );

        if(paymentInfo.status() == PaymentStatus.FAILED) {
            // 결제 실패 시 주문 취소
            order.getOrderItems().forEach(orderItem -> {
                stockService.increase(StockCommand.Increase.of(orderItem.getProductId(), orderItem.getQuantity()));
            });
            order = orderService.cancel(order.getOrderId());
        } else if(paymentInfo.status() == PaymentStatus.COMPLETED) {
            // 포인트로 결제한 경우
            order = orderService.complete(order.getOrderId());
        }

        return OrderResult.Order.from(order);
    }

    @Transactional
    public OrderResult.Order callback(OrderCriteria.Callback criteria) {
        // 주문 조회
        Order order = orderService.getByOrderId(criteria.orderId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문: " + criteria.orderId() )
        );

        PgResult.Find pgResult = pgService.findByTransactionKey(criteria.transactionKey(), order.getUserId());
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
        order = orderService.complete(criteria.orderId());
        paymentService.paymentComplete(order.getOrderId(), pgResult.transactionKey());

        return OrderResult.Order.from(order);
    }

    @Transactional
    public void syncPayment(LocalDateTime currentTime) {
        // 주문 정보 조회
        List<Order> orders = orderService.getPendingOrders();

        for(Order order : orders) {
            if (!order.isExpire(currentTime)) {
                continue;
            }
            List<PgResult.Find> pgResults = pgService.findByOrderId(order.getOrderId(), order.getUserId());

            boolean isNotPaid = pgResults.isEmpty();
            String reason = "주문 정보가 없습니다.";
            for(PgResult.Find pgResult : pgResults) {
                isNotPaid = true;
                if(pgResult.status().equals("SUCCESS")) {
                    // 결제 정보가 있는 경우 주문 만료 처리
                    orderService.complete(order.getOrderId());
                    paymentService.paymentComplete(order.getOrderId(), "AUTO_CANCEL");
                    isNotPaid = false;
                    break;
                }
            }

            if(isNotPaid) {
                // 결제 정보가 없거나 실패한 경우 주문 취소
                orderService.cancel(order.getOrderId());

                // 재고 복구
                order.getOrderItems().forEach(orderItem -> {
                    stockService.increase(StockCommand.Increase.of(orderItem.getProductId(), orderItem.getQuantity()));
                });

                paymentService.paymentFail(order.getOrderId(), reason);
            }
        }
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
