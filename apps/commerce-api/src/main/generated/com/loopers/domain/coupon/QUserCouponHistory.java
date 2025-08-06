package com.loopers.domain.coupon;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserCouponHistory is a Querydsl query type for UserCouponHistory
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserCouponHistory extends BeanPath<UserCouponHistory> {

    private static final long serialVersionUID = 98282604L;

    public static final QUserCouponHistory userCouponHistory = new QUserCouponHistory("userCouponHistory");

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final EnumPath<CouponHistoryType> usedType = createEnum("usedType", CouponHistoryType.class);

    public QUserCouponHistory(String variable) {
        super(UserCouponHistory.class, forVariable(variable));
    }

    public QUserCouponHistory(Path<? extends UserCouponHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserCouponHistory(PathMetadata metadata) {
        super(UserCouponHistory.class, metadata);
    }

}

