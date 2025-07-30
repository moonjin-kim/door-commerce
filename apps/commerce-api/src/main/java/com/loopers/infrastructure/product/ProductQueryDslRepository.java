package com.loopers.infrastructure.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.vo.ProductStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.domain.product.QProduct.product;

@RequiredArgsConstructor
@Component
public class ProductQueryDslRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public PageResponse<Product> search(PageRequest<ProductParams.Search> param) {
        List<Product> items = jpaQueryFactory.selectFrom(product)
                .limit(param.limit())
                .offset(param.offset())
                .where(
                        product.status.eq(ProductStatus.SALE),
                        eqBrandId(param.getParams().brandId())
                )
                .orderBy(getOrderSpecifier(param.getParams().sort()))
                .fetch();

        Long totalCount = jpaQueryFactory.select(product.count())
                .from(product)
                .where(
                        product.status.eq(ProductStatus.SALE),
                        eqBrandId(param.getParams().brandId()) // 동일한 where 조건 추가
                )
                .fetchOne();

        long count = totalCount != null ? totalCount : 0L;

        return PageResponse.of(param.getPage(),param.getSize(), count, items);
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
