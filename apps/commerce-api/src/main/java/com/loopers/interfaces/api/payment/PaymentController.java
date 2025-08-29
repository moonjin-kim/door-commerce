package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.payment.PaymentService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.OrderV1Request;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentV1ApiSpec {
    private final PaymentFacade paymentFacade;

    @PostMapping("/callback")
    @Override
    public ApiResponse<?> callback(@RequestBody PaymentV1Request.Callback request) {
        paymentFacade.callback(request.toCriteria());
        return ApiResponse.success();
    }
}
