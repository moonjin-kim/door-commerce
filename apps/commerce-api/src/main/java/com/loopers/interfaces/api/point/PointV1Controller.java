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
    public ApiResponse<PointV1ResponseDto.PointBalance> register(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody PointV1RequestDto.PointChargeRequest body
    ) {
        PointInfo info = pointFacade.charge(userId, body);
        PointV1ResponseDto.PointBalance response = PointV1ResponseDto.PointBalance.from(info);
        return ApiResponse.success(response);
    }


    @GetMapping("")
    @Override
    public ApiResponse<PointV1ResponseDto.PointBalance> getBalance(@RequestHeader("X-USER-ID") Long userId) {
        PointInfo info = pointFacade.getBalance(userId);
        PointV1ResponseDto.PointBalance response = PointV1ResponseDto.PointBalance.from(info);
        return ApiResponse.success(response);
    }

}
