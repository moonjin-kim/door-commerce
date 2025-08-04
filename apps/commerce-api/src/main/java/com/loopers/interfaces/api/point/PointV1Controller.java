package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointResult;
import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec{
    private final PointFacade pointFacade;

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointV1Response.PointBalance> register(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody PointV1Request.PointChargeRequest body
    ) {
        PointResult info = pointFacade.charge(userId, body);
        PointV1Response.PointBalance response = PointV1Response.PointBalance.from(info);
        return ApiResponse.success(response);
    }


    @GetMapping("")
    @Override
    public ApiResponse<PointV1Response.PointBalance> getBalance(@RequestHeader("X-USER-ID") Long userId) {
        PointResult info = pointFacade.getBalance(userId);
        PointV1Response.PointBalance response = PointV1Response.PointBalance.from(info);
        return ApiResponse.success(response);
    }

}
