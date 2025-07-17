package com.loopers.interfaces.api.point;

import jakarta.validation.constraints.NotNull;

public class PointV1RequestDto {
    public record PointChargeRequest(
            @NotNull
            int amount
    ) {
    }
}
