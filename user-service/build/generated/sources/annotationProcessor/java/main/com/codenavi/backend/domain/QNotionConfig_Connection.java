package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotionConfig_Connection is a Querydsl query type for Connection
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionConfig_Connection extends BeanPath<NotionConfig.Connection> {

    private static final long serialVersionUID = 217815411L;

    public static final QNotionConfig_Connection connection = new QNotionConfig_Connection("connection");

    public final DateTimePath<java.time.LocalDateTime> connectedAt = createDateTime("connectedAt", java.time.LocalDateTime.class);

    public final StringPath connectionStatus = createString("connectionStatus");

    public final StringPath integrationId = createString("integrationId");

    public final StringPath notionToken = createString("notionToken");

    public final StringPath workspaceId = createString("workspaceId");

    public final StringPath workspaceName = createString("workspaceName");

    public QNotionConfig_Connection(String variable) {
        super(NotionConfig.Connection.class, forVariable(variable));
    }

    public QNotionConfig_Connection(Path<? extends NotionConfig.Connection> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionConfig_Connection(PathMetadata metadata) {
        super(NotionConfig.Connection.class, metadata);
    }

}

