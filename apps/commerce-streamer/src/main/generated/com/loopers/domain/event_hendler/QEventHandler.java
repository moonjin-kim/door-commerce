package com.loopers.domain.event_hendler;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEventHandler is a Querydsl query type for EventHandler
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventHandler extends EntityPathBase<EventHandler> {

    private static final long serialVersionUID = 655879086L;

    public static final QEventHandler eventHandler = new QEventHandler("eventHandler");

    public final com.loopers.domain.QBaseEntity _super = new com.loopers.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> deletedAt = _super.deletedAt;

    public final StringPath eventId = createString("eventId");

    public final StringPath groupId = createString("groupId");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> updatedAt = _super.updatedAt;

    public QEventHandler(String variable) {
        super(EventHandler.class, forVariable(variable));
    }

    public QEventHandler(Path<? extends EventHandler> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEventHandler(PathMetadata metadata) {
        super(EventHandler.class, metadata);
    }

}

