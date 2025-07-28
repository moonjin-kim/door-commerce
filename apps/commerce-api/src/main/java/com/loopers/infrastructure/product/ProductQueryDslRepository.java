package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.domain.product.QProduct.product;

@RequiredArgsConstructor
@Component
public class ProductQueryDslRepository implements ProductCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public ProductInfo.ProductPage search(ProductParams.Search postSearch) {

        List<Product> items = jpaQueryFactory.selectFrom(product)
                .limit(postSearch.limit())
                .offset(postSearch.offset())
                .where(
                        product.status.eq(ProductStatus.SALE),
                        eqBrandId(postSearch.brandId())
                )
                .orderBy(getOrderSpecifier(postSearch.sort()))
                .fetch();

        Long totalCount = jpaQueryFactory.select(product.count())
                .from(product)
                .where(
                        product.status.eq(ProductStatus.SALE),
                        eqBrandId(postSearch.brandId()) // 동일한 where 조건 추가
                )
                .fetchOne();

        long count = totalCount != null ? totalCount : 0L;

        return new ProductInfo.ProductPage(postSearch.limit(),postSearch.offset(),
                count, items);
    }

    private BooleanExpression eqBrandId(Long brandId) {
        if(brandId == null) {
            return null;
        }

        return product.brandId.eq(brandId);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<>(Order.DESC, product.id);
        }

        if (sort.equals("price_asc")) {
            return new OrderSpecifier<>(Order.ASC, product.price.price);
        }

        return new OrderSpecifier<>(Order.DESC, product.id);
    }

}
