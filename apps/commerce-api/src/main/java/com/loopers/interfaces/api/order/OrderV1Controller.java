package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderResult;
import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec{
    private final OrderFacade orderFacade;

    @PostMapping("")
    @Override
    public ApiResponse<OrderV1Response.Order> order(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody OrderV1Request.Order request
    ) {
        OrderResult.Order orders = orderFacade.order(request.toCriteria(userId));

        return ApiResponse.success(OrderV1Response.Order.from(orders));
    }

    @PostMapping("/callback")
    @Override
    public ApiResponse<?> callback(
            @RequestBody OrderV1Request.Callback request
    ) {
        System.out.println("callback request = " + request);

        return ApiResponse.success(orderFacade.callback(request.toCriteria()));
    }

    @GetMapping("/{orderId}")
    @Override
    public ApiResponse<OrderV1Response.Order> getBy(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable(value = "orderId") Long orderId
    ) {
        OrderResult.Order orders = orderFacade.getBy(orderId, userId);

        return ApiResponse.success(OrderV1Response.Order.from(orders));
    }

    @GetMapping()
    @Override
    public ApiResponse<PageResponse<OrderV1Response.Order>> getOrdersBy(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        PageRequest<OrderCriteria.GetOrdersBy> pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                OrderCriteria.GetOrdersBy.of(userId)
        );
        PageResponse<OrderResult.Order> orders = orderFacade.getOrders(pageRequest);

        return ApiResponse.success(
                orders.map(
                        OrderV1Response.Order::from
                )
        );
    }

}
