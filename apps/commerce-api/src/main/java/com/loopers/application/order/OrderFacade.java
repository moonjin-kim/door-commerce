package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final StockService stockService;
    private final PointService pointService;
    private final UserService userService;

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
        OrderInfo.OrderDto orderInfo = orderService.order(OrderCommand.Order.of(criteria.userId(), orderItems));

        // 포인트 사용
        pointService.using(PointCommand.Using.of(orderInfo.userId(), orderInfo.orderId(), orderInfo.totalPrice()));

        // 재고 차감
        List<StockCommand.Decrease> stockCommands = orderItems.stream()
                .map(item -> StockCommand.Decrease.of(item.productId(), item.quantity()))
                .toList();
        stockService.decreaseAll(stockCommands);

        return OrderResult.Order.of(orderInfo);
    }
}
