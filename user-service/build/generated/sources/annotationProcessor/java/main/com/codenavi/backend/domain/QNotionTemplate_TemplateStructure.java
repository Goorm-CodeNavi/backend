package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotionTemplate_TemplateStructure is a Querydsl query type for TemplateStructure
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionTemplate_TemplateStructure extends BeanPath<NotionTemplate.TemplateStructure> {

    private static final long serialVersionUID = 743739228L;

    public static final QNotionTemplate_TemplateStructure templateStructure = new QNotionTemplate_TemplateStructure("templateStructure");

    public final ListPath<NotionTemplate.Block, QNotionTemplate_Block> blocks = this.<NotionTemplate.Block, QNotionTemplate_Block>createList("blocks", NotionTemplate.Block.class, QNotionTemplate_Block.class, PathInits.DIRECT2);

    public final ListPath<NotionTemplate.Property, QNotionTemplate_Property> properties = this.<NotionTemplate.Property, QNotionTemplate_Property>createList("properties", NotionTemplate.Property.class, QNotionTemplate_Property.class, PathInits.DIRECT2);

    public QNotionTemplate_TemplateStructure(String variable) {
        super(NotionTemplate.TemplateStructure.class, forVariable(variable));
    }

    public QNotionTemplate_TemplateStructure(Path<? extends NotionTemplate.TemplateStructure> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionTemplate_TemplateStructure(PathMetadata metadata) {
        super(NotionTemplate.TemplateStructure.class, metadata);
    }

}

