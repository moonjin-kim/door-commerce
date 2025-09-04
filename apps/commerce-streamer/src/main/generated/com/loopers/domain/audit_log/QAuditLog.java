package com.loopers.domain.audit_log;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuditLog is a Querydsl query type for AuditLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuditLog extends EntityPathBase<AuditLog> {

    private static final long serialVersionUID = 704631912L;

    public static final QAuditLog auditLog = new QAuditLog("auditLog");

    public final com.loopers.domain.QBaseEntity _super = new com.loopers.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> deletedAt = _super.deletedAt;

    public final StringPath eventId = createString("eventId");

    public final StringPath eventType = createString("eventType");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath payload = createString("payload");

    public final DateTimePath<java.time.LocalDateTime> publishedAt = createDateTime("publishedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> updatedAt = _super.updatedAt;

    public final StringPath version = createString("version");

    public QAuditLog(String variable) {
        super(AuditLog.class, forVariable(variable));
    }

    public QAuditLog(Path<? extends AuditLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuditLog(PathMetadata metadata) {
        super(AuditLog.class, metadata);
    }

}

