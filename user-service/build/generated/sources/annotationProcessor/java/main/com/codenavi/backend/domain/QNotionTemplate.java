package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotionTemplate is a Querydsl query type for NotionTemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotionTemplate extends EntityPathBase<NotionTemplate> {

    private static final long serialVersionUID = -483080687L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotionTemplate notionTemplate = new QNotionTemplate("notionTemplate");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath category = createString("category");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QNotionTemplate_Metadata metadata;

    public final StringPath name = createString("name");

    public final StringPath templateId = createString("templateId");

    public final QNotionTemplate_TemplateStructure templateStructure;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<NotionTemplate.VariableMapping, QNotionTemplate_VariableMapping> variableMappings = this.<NotionTemplate.VariableMapping, QNotionTemplate_VariableMapping>createList("variableMappings", NotionTemplate.VariableMapping.class, QNotionTemplate_VariableMapping.class, PathInits.DIRECT2);

    public final QNotionTemplate_Version version;

    public QNotionTemplate(String variable) {
        this(NotionTemplate.class, forVariable(variable), INITS);
    }

    public QNotionTemplate(Path<? extends NotionTemplate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotionTemplate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotionTemplate(PathMetadata metadata, PathInits inits) {
        this(NotionTemplate.class, metadata, inits);
    }

    public QNotionTemplate(Class<? extends NotionTemplate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.metadata = inits.isInitialized("metadata") ? new QNotionTemplate_Metadata(forProperty("metadata")) : null;
        this.templateStructure = inits.isInitialized("templateStructure") ? new QNotionTemplate_TemplateStructure(forProperty("templateStructure")) : null;
        this.version = inits.isInitialized("version") ? new QNotionTemplate_Version(forProperty("version")) : null;
    }

}

