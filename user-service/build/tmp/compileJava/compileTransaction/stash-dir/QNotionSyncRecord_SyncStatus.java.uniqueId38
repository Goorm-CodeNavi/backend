package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotionSyncRecord_SyncStatus is a Querydsl query type for SyncStatus
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionSyncRecord_SyncStatus extends BeanPath<NotionSyncRecord.SyncStatus> {

    private static final long serialVersionUID = 1985903288L;

    public static final QNotionSyncRecord_SyncStatus syncStatus = new QNotionSyncRecord_SyncStatus("syncStatus");

    public final NumberPath<Integer> attemptCount = createNumber("attemptCount", Integer.class);

    public final StringPath errorMessage = createString("errorMessage");

    public final DateTimePath<java.time.LocalDateTime> lastAttemptAt = createDateTime("lastAttemptAt", java.time.LocalDateTime.class);

    public final StringPath status = createString("status");

    public QNotionSyncRecord_SyncStatus(String variable) {
        super(NotionSyncRecord.SyncStatus.class, forVariable(variable));
    }

    public QNotionSyncRecord_SyncStatus(Path<? extends NotionSyncRecord.SyncStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionSyncRecord_SyncStatus(PathMetadata metadata) {
        super(NotionSyncRecord.SyncStatus.class, metadata);
    }

}

