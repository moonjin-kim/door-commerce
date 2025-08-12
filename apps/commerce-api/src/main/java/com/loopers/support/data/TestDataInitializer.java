package com.loopers.support.data;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.point.Point;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.stock.StockJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Profile("new")
@RequiredArgsConstructor
public class TestDataInitializer implements ApplicationRunner {

    private final BrandJpaRepository brandJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final StockJpaRepository stockJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PointJpaRepository pointJpaRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // --- 1. 브랜드 및 상품 데이터 생성 ---
        List<Brand> brands = new ArrayList<>();
        List<Product> totalProducts = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            // 브랜드 생성
            BrandCommand.Create brandCommand = new BrandCommand.Create(
                    "테스트 브랜드 " + i,
                    "테스트를 위한 브랜드입니다. No." + i,
                    "https://test.com/logo-" + i + ".png"
            );
            Brand savedBrand = brandJpaRepository.save(Brand.create(brandCommand));
            brands.add(savedBrand);

            // 각 브랜드별로 100~150개의 상품 생성
            int productCount = ThreadLocalRandom.current().nextInt(1000, 1100);
            List<Product> productsForBrand = new ArrayList<>();
            for (int j = 1; j <= productCount; j++) {
                // 고유한 상품명을 위해 브랜드 ID와 상품 인덱스 조합
                long uniqueProductIdSuffix = (i * 1000) + j;
                ProductCommand.Create productCommand = new ProductCommand.Create(
                        savedBrand.getId(),
                        "브랜드 " + i + "의 상품 " + j,
                        "상품 설명 브랜드 " + i + " 상품 " + uniqueProductIdSuffix,
                        "https://test.com/band - " + i + " product-" + uniqueProductIdSuffix + ".png",
                        ThreadLocalRandom.current().nextLong(10000, 200001) // 1만원 ~ 20만원
                );
                productsForBrand.add(Product.create(productCommand));
            }
            totalProducts.addAll(productsForBrand);
        }

        // 모든 상품과 재고를 한 번에 저장
        totalProducts = productJpaRepository.saveAll(totalProducts);

        List<Stock> stocks = totalProducts.stream()
                .map(product -> Stock.create(StockCommand.Create.of(product.getId(), 100)))
                .toList();
        stockJpaRepository.saveAll(stocks);


        // --- 2. 유저 및 포인트 데이터 생성 ---
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            UserCommand.Create command = new UserCommand.Create(
                    "테스트유저" + i,
                    "tester" + i,
                    "test" + i + "@email.com",
                    "2000-01-01",
                    Gender.MALE
            );
            users.add(User.create(command));
        }
        userJpaRepository.saveAll(users);

        List<Point> points = users.stream()
                .map(user -> {
                    Point point = Point.create(user.getId());
                    point.charge(1000000L); // 각 유저에게 100만원 포인트 충전
                    return point;
                })
                .toList();
        pointJpaRepository.saveAll(points);
    }
}
