package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;
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
    public ApiResponse<PointV1ResponseDto.PointBalanceResponse> register(
            @RequestHeader("X-USER-ID") String account,
            @RequestBody PointV1RequestDto.PointChargeRequest body
    ) {
        PointInfo info = pointFacade.charge(account, body);
        PointV1ResponseDto.PointBalanceResponse response = PointV1ResponseDto.PointBalanceResponse.from(info);
        return ApiResponse.success(response);
    }


    @GetMapping("")
    @Override
    public ApiResponse<PointV1ResponseDto.PointBalanceResponse> getBalance(@RequestHeader("X-USER-ID") String account) {
        PointInfo info = pointFacade.getBalance(account);
        PointV1ResponseDto.PointBalanceResponse response = PointV1ResponseDto.PointBalanceResponse.from(info);
        return ApiResponse.success(response);
    }

}
