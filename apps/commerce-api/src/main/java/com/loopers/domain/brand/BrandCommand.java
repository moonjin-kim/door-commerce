package com.loopers.domain.brand;

public class BrandCommand {
    public record Create(String name, String description, String logoUrl) {
        public static Create of(String name, String description, String logoUrl) {
            return new Create(name, description, logoUrl);
        }

    }
}
