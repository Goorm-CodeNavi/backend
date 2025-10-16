package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotionSyncRecord is a Querydsl query type for NotionSyncRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotionSyncRecord extends EntityPathBase<NotionSyncRecord> {

    private static final long serialVersionUID = 372097699L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotionSyncRecord notionSyncRecord = new QNotionSyncRecord("notionSyncRecord");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QNotionSyncRecord_NotionPage notionPage;

    public final QSolution solution;

    public final QNotionSyncRecord_SyncDetails syncDetails;

    public final QNotionSyncRecord_SyncStatus syncStatus;

    public final QNotionSyncRecord_SyncTarget syncTarget;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public final QNotionSyncRecord_VersionInfo versionInfo;

    public QNotionSyncRecord(String variable) {
        this(NotionSyncRecord.class, forVariable(variable), INITS);
    }

    public QNotionSyncRecord(Path<? extends NotionSyncRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotionSyncRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotionSyncRecord(PathMetadata metadata, PathInits inits) {
        this(NotionSyncRecord.class, metadata, inits);
    }

    public QNotionSyncRecord(Class<? extends NotionSyncRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.notionPage = inits.isInitialized("notionPage") ? new QNotionSyncRecord_NotionPage(forProperty("notionPage")) : null;
        this.solution = inits.isInitialized("solution") ? new QSolution(forProperty("solution"), inits.get("solution")) : null;
        this.syncDetails = inits.isInitialized("syncDetails") ? new QNotionSyncRecord_SyncDetails(forProperty("syncDetails")) : null;
        this.syncStatus = inits.isInitialized("syncStatus") ? new QNotionSyncRecord_SyncStatus(forProperty("syncStatus")) : null;
        this.syncTarget = inits.isInitialized("syncTarget") ? new QNotionSyncRecord_SyncTarget(forProperty("syncTarget")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
        this.versionInfo = inits.isInitialized("versionInfo") ? new QNotionSyncRecord_VersionInfo(forProperty("versionInfo")) : null;
    }

}

