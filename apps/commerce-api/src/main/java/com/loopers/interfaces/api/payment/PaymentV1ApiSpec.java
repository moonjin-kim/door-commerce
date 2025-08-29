package com.loopers.interfaces.api.payment;

import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.OrderV1Request;
import com.loopers.interfaces.api.order.OrderV1Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

@Tag(name = "Order V1 API", description = "Order API 입니다.")
public interface PaymentV1ApiSpec {

    @Operation(
            summary = "결제 콜백",
            description = "결제 콜백"
    )
    ApiResponse<?> callback(PaymentV1Request.Callback request);
}
