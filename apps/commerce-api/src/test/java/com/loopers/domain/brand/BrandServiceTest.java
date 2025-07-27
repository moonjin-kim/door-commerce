package com.loopers.domain.brand;

import com.loopers.domain.user.UserService;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BrandServiceTest {
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private BrandService brandService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드를 조회할 때")
    @Nested
    class FindBy {
        @DisplayName("존재하지 않는 브랜드 아이디가 주어지면 빈 Optional을 반환한다.")
        @Test
        void returnEmpty_whenBrandIdNotExist(){
            //given
            Long brandId = 1L;

            //when
            Optional<Brand> foundBrand = brandService.findBy(brandId);

            //then
            assertAll(
                    () -> assertFalse(foundBrand.isPresent())
            );
        }

        @DisplayName("조회할 상품의 아이디가 주어지면 해당 브랜드를 반환한다.")
        @Test
        void returnBand_whenBrandIdIsProvider(){
            //given
            var brand = brandJpaRepository.save(
                    Brand.create(BrandCommand.Create.of(
                            "루퍼스",
                            "루퍼스는 루퍼스입니다.",
                            "https://loopers.com/logo.png"
                    ))
            );

            //when
            Optional<Brand> foundBrand = brandService.findBy(brand.getId());

            //then
            assertAll(
                    () -> assertTrue(foundBrand.isPresent()),
                    () -> assertEquals(brand.getId(), foundBrand.get().getId()),
                    () -> assertEquals(brand.getName(), foundBrand.get().getName()),
                    () -> assertEquals(brand.getDescription(), foundBrand.get().getDescription()),
                    () -> assertEquals(brand.getLogoUrl(), foundBrand.get().getLogoUrl())
            );
        }
    }
}
