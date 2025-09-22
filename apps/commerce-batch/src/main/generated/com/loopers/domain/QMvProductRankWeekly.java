package com.loopers.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMvProductRankWeekly is a Querydsl query type for MvProductRankWeekly
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMvProductRankWeekly extends EntityPathBase<MvProductRankWeekly> {

    private static final long serialVersionUID = 110016482L;

    public static final QMvProductRankWeekly mvProductRankWeekly = new QMvProductRankWeekly("mvProductRankWeekly");

    public final DatePath<java.time.LocalDate> aggDate = createDate("aggDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.ZonedDateTime> createdAt = createDateTime("createdAt", java.time.ZonedDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<java.math.BigDecimal> totalScore = createNumber("totalScore", java.math.BigDecimal.class);

    public final DateTimePath<java.time.ZonedDateTime> updatedAt = createDateTime("updatedAt", java.time.ZonedDateTime.class);

    public QMvProductRankWeekly(String variable) {
        super(MvProductRankWeekly.class, forVariable(variable));
    }

    public QMvProductRankWeekly(Path<? extends MvProductRankWeekly> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMvProductRankWeekly(PathMetadata metadata) {
        super(MvProductRankWeekly.class, metadata);
    }

}

