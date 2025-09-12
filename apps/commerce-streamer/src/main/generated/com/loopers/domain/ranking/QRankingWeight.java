package com.loopers.domain.ranking;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRankingWeight is a Querydsl query type for RankingWeight
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRankingWeight extends EntityPathBase<RankingWeight> {

    private static final long serialVersionUID = 1600362309L;

    public static final QRankingWeight rankingWeight = new QRankingWeight("rankingWeight");

    public final com.loopers.domain.QBaseEntity _super = new com.loopers.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Double> likeWeight = createNumber("likeWeight", Double.class);

    public final NumberPath<Double> orderWeight = createNumber("orderWeight", Double.class);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Double> viewWeight = createNumber("viewWeight", Double.class);

    public final StringPath weightName = createString("weightName");

    public QRankingWeight(String variable) {
        super(RankingWeight.class, forVariable(variable));
    }

    public QRankingWeight(Path<? extends RankingWeight> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRankingWeight(PathMetadata metadata) {
        super(RankingWeight.class, metadata);
    }

}

