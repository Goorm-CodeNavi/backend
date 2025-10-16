package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotionConfig is a Querydsl query type for NotionConfig
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotionConfig extends EntityPathBase<NotionConfig> {

    private static final long serialVersionUID = 1573451417L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotionConfig notionConfig = new QNotionConfig("notionConfig");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final QNotionConfig_Connection connection;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QNotionConfig_SyncSettings syncSettings;

    public final QNotionConfig_TemplatePreferences templatePreferences;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public final QNotionConfig_WorkspaceStructure workspaceStructure;

    public QNotionConfig(String variable) {
        this(NotionConfig.class, forVariable(variable), INITS);
    }

    public QNotionConfig(Path<? extends NotionConfig> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotionConfig(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotionConfig(PathMetadata metadata, PathInits inits) {
        this(NotionConfig.class, metadata, inits);
    }

    public QNotionConfig(Class<? extends NotionConfig> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.connection = inits.isInitialized("connection") ? new QNotionConfig_Connection(forProperty("connection")) : null;
        this.syncSettings = inits.isInitialized("syncSettings") ? new QNotionConfig_SyncSettings(forProperty("syncSettings")) : null;
        this.templatePreferences = inits.isInitialized("templatePreferences") ? new QNotionConfig_TemplatePreferences(forProperty("templatePreferences")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
        this.workspaceStructure = inits.isInitialized("workspaceStructure") ? new QNotionConfig_WorkspaceStructure(forProperty("workspaceStructure"), inits.get("workspaceStructure")) : null;
    }

}

