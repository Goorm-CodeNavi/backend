package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotionConfig_SyncSettings is a Querydsl query type for SyncSettings
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionConfig_SyncSettings extends BeanPath<NotionConfig.SyncSettings> {

    private static final long serialVersionUID = -970770125L;

    public static final QNotionConfig_SyncSettings syncSettings = new QNotionConfig_SyncSettings("syncSettings");

    public final BooleanPath autoSyncEnabled = createBoolean("autoSyncEnabled");

    public final BooleanPath syncAiFeedback = createBoolean("syncAiFeedback");

    public final BooleanPath syncCode = createBoolean("syncCode");

    public final StringPath syncFrequency = createString("syncFrequency");

    public final BooleanPath syncThinkingProcess = createBoolean("syncThinkingProcess");

    public QNotionConfig_SyncSettings(String variable) {
        super(NotionConfig.SyncSettings.class, forVariable(variable));
    }

    public QNotionConfig_SyncSettings(Path<? extends NotionConfig.SyncSettings> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionConfig_SyncSettings(PathMetadata metadata) {
        super(NotionConfig.SyncSettings.class, metadata);
    }

}

