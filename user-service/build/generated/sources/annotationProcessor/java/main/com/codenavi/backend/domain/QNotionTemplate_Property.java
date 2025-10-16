package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotionTemplate_Property is a Querydsl query type for Property
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionTemplate_Property extends BeanPath<NotionTemplate.Property> {

    private static final long serialVersionUID = -358698702L;

    public static final QNotionTemplate_Property property = new QNotionTemplate_Property("property");

    public final StringPath name = createString("name");

    public final StringPath options = createString("options");

    public final StringPath type = createString("type");

    public QNotionTemplate_Property(String variable) {
        super(NotionTemplate.Property.class, forVariable(variable));
    }

    public QNotionTemplate_Property(Path<? extends NotionTemplate.Property> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionTemplate_Property(PathMetadata metadata) {
        super(NotionTemplate.Property.class, metadata);
    }

}

