package com.loopers.interfaces.api.point;

import com.loopers.domain.point.PointCommand;
import jakarta.validation.constraints.NotNull;

public class PointV1Request {
    public record PointChargeRequest(
            @NotNull
            Long amount
    ) {

        public PointCommand.Charge toCommand(Long userId) {
            return new PointCommand.Charge(userId, amount);
        }
    }
}
