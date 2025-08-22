package com.loopers.infrastructure.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.product.ProductView;
import com.loopers.domain.product.vo.ProductStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.domain.like.QLike.like;
import static com.loopers.domain.product.QProduct.product;

@RequiredArgsConstructor
@Component
public class ProductQueryDslRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public PageResponse<ProductView> search(PageRequest<ProductParams.Search> param) {
        List<ProductView> items = jpaQueryFactory
                .select(Projections.constructor(ProductView.class,
                        product.id,
                        product.name,
                        product.brandId,
                        product.description,
                        product.imageUrl,
                        product.price.value, // Amount 객체의 값을 가져옴
                        product.status,
                        product.likeCount
                ))
                .from(product)
                .limit(param.limit())
                .offset(param.offset())
                .where(
                        product.status.eq(ProductStatus.SALE),
                        eqBrandId(param.getParams().brandId())
                )
                .orderBy(getOrderSpecifier(param.getParams().sort()))
                .fetch();

        return PageResponse.of(param.getPage(),param.getSize(), items);
    }

    public Long searchCount(ProductParams.SearchCount param) {

        Long totalCount = jpaQueryFactory.select(product.count())
                .from(product)
                .where(
                        product.status.eq(ProductStatus.SALE),
                        eqBrandId(param.brandId()) // 동일한 where 조건 추가
                )
                .fetchOne();

        return totalCount != null ? totalCount : 0L;
    }

    private BooleanExpression eqBrandId(Long brandId) {
        if(brandId == null) {
            return null;
        }

        return product.brandId.eq(brandId);
    }

    private OrderSpecifier<?> getOrderSpecifier(ProductParams.ProductSortOption sort) {
        if (sort.equals(ProductParams.ProductSortOption.PRICE_ASC)) {
            return new OrderSpecifier<>(Order.ASC, product.price.value);
        }

        if(sort.equals(ProductParams.ProductSortOption.LIKE_DESC)) {
            return new OrderSpecifier<>(Order.DESC, product.likeCount);

        }
//        if(sort.equals(ProductParams.ProductSortOption.LIKE_DESC)) {
//            return new OrderSpecifier<>(Order.DESC, product.likeCount);
//
//        }

        if(sort.equals(ProductParams.ProductSortOption.LATEST)) {
            return new OrderSpecifier<>(Order.DESC, product.id);
        }

        return new OrderSpecifier<>(Order.DESC, product.id);
    }

}
