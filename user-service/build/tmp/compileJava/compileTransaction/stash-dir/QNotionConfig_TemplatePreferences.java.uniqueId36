package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotionConfig_TemplatePreferences is a Querydsl query type for TemplatePreferences
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionConfig_TemplatePreferences extends BeanPath<NotionConfig.TemplatePreferences> {

    private static final long serialVersionUID = 1373636393L;

    public static final QNotionConfig_TemplatePreferences templatePreferences = new QNotionConfig_TemplatePreferences("templatePreferences");

    public final ListPath<String, StringPath> customTemplates = this.<String, StringPath>createList("customTemplates", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath defaultProblemTemplate = createString("defaultProblemTemplate");

    public QNotionConfig_TemplatePreferences(String variable) {
        super(NotionConfig.TemplatePreferences.class, forVariable(variable));
    }

    public QNotionConfig_TemplatePreferences(Path<? extends NotionConfig.TemplatePreferences> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionConfig_TemplatePreferences(PathMetadata metadata) {
        super(NotionConfig.TemplatePreferences.class, metadata);
    }

}

