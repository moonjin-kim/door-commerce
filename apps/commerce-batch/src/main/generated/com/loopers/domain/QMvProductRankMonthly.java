package com.loopers.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMvProductRankMonthly is a Querydsl query type for MvProductRankMonthly
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMvProductRankMonthly extends EntityPathBase<MvProductRankMonthly> {

    private static final long serialVersionUID = -874691380L;

    public static final QMvProductRankMonthly mvProductRankMonthly = new QMvProductRankMonthly("mvProductRankMonthly");

    public final DatePath<java.time.LocalDate> aggDate = createDate("aggDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.ZonedDateTime> createdAt = createDateTime("createdAt", java.time.ZonedDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<java.math.BigDecimal> totalScore = createNumber("totalScore", java.math.BigDecimal.class);

    public final DateTimePath<java.time.ZonedDateTime> updatedAt = createDateTime("updatedAt", java.time.ZonedDateTime.class);

    public QMvProductRankMonthly(String variable) {
        super(MvProductRankMonthly.class, forVariable(variable));
    }

    public QMvProductRankMonthly(Path<? extends MvProductRankMonthly> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMvProductRankMonthly(PathMetadata metadata) {
        super(MvProductRankMonthly.class, metadata);
    }

}

