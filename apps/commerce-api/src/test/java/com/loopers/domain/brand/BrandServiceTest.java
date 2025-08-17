package com.loopers.domain.brand;

import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.support.cache.CacheRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BrandServiceTest {
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private BrandService brandService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @MockitoSpyBean
    private CacheRepository cacheRepository;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드를 조회할 때")
    @Nested
    class FindBy {
        @DisplayName("존재하지 않는 브랜드 아이디가 주어지면 빈 Optional을 반환한다.")
        @Test
        void throwNotFound_whenBrandIdNotExist(){
            //given
            Long brandId = 1L;

            //when
            Optional<Brand> result = brandService.getBy(brandId);

            //then
            assertThat(result.isPresent()).isFalse();
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
            Brand foundBrand = brandService.getBy(brand.getId()).get();

            //then
            assertAll(
                    () -> assertEquals(brand.getId(), foundBrand.getId()),
                    () -> assertEquals(brand.getName(), foundBrand.getName()),
                    () -> assertEquals(brand.getDescription(), foundBrand.getDescription()),
                    () -> assertEquals(brand.getLogoUrl(), foundBrand.getLogoUrl())
            );
        }

        @DisplayName("조회할 상품의 아이디가 주어지면 해당 브랜드를 반환한다.")
        @Test
        void saveBandCache_whenBrandIdIsProvider(){
            //given
            var brand = brandJpaRepository.save(
                    Brand.create(BrandCommand.Create.of(
                            "루퍼스",
                            "루퍼스는 루퍼스입니다.",
                            "https://loopers.com/logo.png"
                    ))
            );
            brandService.getBy(brand.getId()).get();

            //when
            Brand foundBrand = brandService.getBy(brand.getId()).get();

            //then
            assertAll(
                () -> assertEquals(brand.getId(), foundBrand.getId()),
                () -> assertEquals(brand.getName(), foundBrand.getName()),
                () -> assertEquals(brand.getDescription(), foundBrand.getDescription()),
                () -> assertEquals(brand.getLogoUrl(), foundBrand.getLogoUrl()),
                () ->assertThat(cacheRepository.get(
                                com.loopers.support.cache.CommerceCache.BrandCache.INSTANCE,
                                brand.getId().toString(),
                                Brand.class
                        )).isPresent()
            );
        }
    }
}
