package com.loopers.interfaces.api.order;

import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Request;
import com.loopers.interfaces.api.point.PointV1Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Order V1 API", description = "Order API 입니다.")
public interface OrderV1ApiSpec {
    @Operation(
            summary = "주문",
            description = "주문"
    )
    ApiResponse<OrderV1Response.Order> order(Long userId, OrderV1Request.Order request);

    @Operation(
            summary = "주문",
            description = "주문"
    )
    ApiResponse<?> callback(OrderV1Request.Callback request);

    @Operation(
            summary = "주문 상세 조회",
            description = "주문"
    )
    ApiResponse<OrderV1Response.Order> getBy(Long userId, Long OrderId);

    @Operation(
            summary = "주문 목록 조회",
            description = "유저의 주문 목록을 조회합니다."
    )
    ApiResponse<PageResponse<OrderV1Response.Order>> getOrdersBy(
            Pageable pageable,
           Long userId
    );
}
