package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotionConfig_WorkspaceStructure is a Querydsl query type for WorkspaceStructure
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionConfig_WorkspaceStructure extends BeanPath<NotionConfig.WorkspaceStructure> {

    private static final long serialVersionUID = -567185933L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotionConfig_WorkspaceStructure workspaceStructure = new QNotionConfig_WorkspaceStructure("workspaceStructure");

    public final QNotionConfig_Databases databases;

    public final QNotionConfig_Pages pages;

    public final StringPath rootPageId = createString("rootPageId");

    public QNotionConfig_WorkspaceStructure(String variable) {
        this(NotionConfig.WorkspaceStructure.class, forVariable(variable), INITS);
    }

    public QNotionConfig_WorkspaceStructure(Path<? extends NotionConfig.WorkspaceStructure> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotionConfig_WorkspaceStructure(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotionConfig_WorkspaceStructure(PathMetadata metadata, PathInits inits) {
        this(NotionConfig.WorkspaceStructure.class, metadata, inits);
    }

    public QNotionConfig_WorkspaceStructure(Class<? extends NotionConfig.WorkspaceStructure> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.databases = inits.isInitialized("databases") ? new QNotionConfig_Databases(forProperty("databases")) : null;
        this.pages = inits.isInitialized("pages") ? new QNotionConfig_Pages(forProperty("pages")) : null;
    }

}

