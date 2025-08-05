package com.loopers.domain.coupon;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserCouponHistory is a Querydsl query type for UserCouponHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCouponHistory extends EntityPathBase<UserCouponHistory> {

    private static final long serialVersionUID = 98282604L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCouponHistory userCouponHistory = new QUserCouponHistory("userCouponHistory");

    public final com.loopers.domain.QBaseEntity _super = new com.loopers.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<CouponHistoryType> usedType = createEnum("usedType", CouponHistoryType.class);

    public final QUserCoupon userCoupon;

    public QUserCouponHistory(String variable) {
        this(UserCouponHistory.class, forVariable(variable), INITS);
    }

    public QUserCouponHistory(Path<? extends UserCouponHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserCouponHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserCouponHistory(PathMetadata metadata, PathInits inits) {
        this(UserCouponHistory.class, metadata, inits);
    }

    public QUserCouponHistory(Class<? extends UserCouponHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userCoupon = inits.isInitialized("userCoupon") ? new QUserCoupon(forProperty("userCoupon"), inits.get("userCoupon")) : null;
    }

}

