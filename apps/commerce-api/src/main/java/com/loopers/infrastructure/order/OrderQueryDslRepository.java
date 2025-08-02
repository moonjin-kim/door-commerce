package com.loopers.infrastructure.order;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.order.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.domain.order.QOrder.order;

@RequiredArgsConstructor
@Component
public class OrderQueryDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public PageResponse<Order> findAllBy(PageRequest<OrderParams.GetOrdersBy> param) {
        List<Order> items = jpaQueryFactory.selectFrom(order)
                .limit(param.limit())
                .offset(param.offset())
                .where(
                        order.userId.eq(param.getParams().userId())
                )
                .fetch();

        Long totalCount = jpaQueryFactory.select(order.count())
                .from(order)
                .where(
                        order.userId.eq(param.getParams().userId())
                )
                .fetchOne();

        long count = totalCount != null ? totalCount : 0L;

        return PageResponse.of(param.getPage(),param.getSize(), count, items);
    }
}
