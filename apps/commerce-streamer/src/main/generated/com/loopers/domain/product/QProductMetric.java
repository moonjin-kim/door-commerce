package com.loopers.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductMetric is a Querydsl query type for ProductMetric
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductMetric extends EntityPathBase<ProductMetric> {

    private static final long serialVersionUID = 1682629999L;

    public static final QProductMetric productMetric = new QProductMetric("productMetric");

    public final com.loopers.domain.QBaseEntity _super = new com.loopers.domain.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> bucket = createDate("bucket", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final NumberPath<Long> orderQuantity = createNumber("orderQuantity", Long.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public QProductMetric(String variable) {
        super(ProductMetric.class, forVariable(variable));
    }

    public QProductMetric(Path<? extends ProductMetric> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductMetric(PathMetadata metadata) {
        super(ProductMetric.class, metadata);
    }

}

