package com.loopers.domain.like;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductLike is a Querydsl query type for ProductLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductLike extends EntityPathBase<ProductLike> {

    private static final long serialVersionUID = -2092842240L;

    public static final QProductLike productLike = new QProductLike("productLike");

    public final com.loopers.domain.QBaseEntity _super = new com.loopers.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QProductLike(String variable) {
        super(ProductLike.class, forVariable(variable));
    }

    public QProductLike(Path<? extends ProductLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductLike(PathMetadata metadata) {
        super(ProductLike.class, metadata);
    }

}

