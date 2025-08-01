package com.loopers.domain.brand;

public enum BrandStatus {
    ACTIVE,
    INACTIVE,
    DELETED;

    public static BrandStatus of(String status) {
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> ACTIVE;
            case "INACTIVE" -> INACTIVE;
            case "DELETED" -> DELETED;
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}
