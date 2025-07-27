package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @DisplayName("상품을 생성할 때,")
    @Nested
    class Create {

        @DisplayName("가격이 0보다 작으면 BadRequest 예외가 발생한다.")
        @Test
        void throwBadRequest_WhenPriceIsNegative() {
            // given
            var command = ProductCommand.Create.of(
                    1L,
                    "상품명",
                    "상품 설명",
                    "https://example.com/image.png",
                    -1L // Invalid price
            );

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                Product.create(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("브랜드 ID가 null이면 BadRequest 예외가 발생한다.")
        @Test
        void throwBadRequest_WhenBrandIdIsNull() {
            // given
            var command = ProductCommand.Create.of(
                    null,
                    "상품명",
                    "상품 설명",
                    "https://example.com/image.png",
                    10000L // Invalid price
            );

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                Product.create(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품명이 유효하지 않으면 BadRequest 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "가나다라마바사아자차카타파하아야어이신발장"
        })
        void throwBadRequest_WhenNameIsNotValid(String name) {
            // given
            var command = ProductCommand.Create.of(
                    1L,
                    name,
                    "상품 설명",
                    "https://example.com/image.png",
                    10000L // Invalid price
            );

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                Product.create(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품설명이 유효하지 않으면 BadRequest 예외가 발생한다.")
        @Test
        void throwBadRequest_WhenDescriptionIsEmpty() {
            // given
            var command = ProductCommand.Create.of(
                    1L,
                    "상품",
                    "",
                    "https://example.com/image.png",
                    10000L // Invalid price
            );

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                Product.create(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("로그 이미지 주소 형식이 잘못되었으면 BadRequest 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "loopers.com/logo.png",
        })
        void throwBadRequest_InValidImageUrl(String imageUrl) {
            // given
            var command = ProductCommand.Create.of(
                    1L,
                    "상품",
                    "",
                    imageUrl,
                    10000L // Invalid price
            );

            // when
            CoreException result = assertThrows(CoreException.class, () -> {
                Product.create(command);
            });

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("유효한 상품 정보가 주어지면, Product 객체를 반환한다.")
        @Test
        void createProduct_WhenValidCommand() {
            // given
            var command = ProductCommand.Create.of(
                    1L,
                    "상품명",
                    "상품 설명",
                    "https://example.com/image.png",
                    10000L // Valid price
            );

            // when
            Product product = Product.create(command);

            // then
            assertNotNull(product);
            assertEquals(command.brandId(), product.getBrandId());
            assertEquals(command.name(), product.getName());
            assertEquals(command.description(), product.getDescription());
            assertEquals(command.imageUrl(), product.getImageUrl());
            assertEquals(command.price(), product.getPrice().getPrice());
        }
    }

}
