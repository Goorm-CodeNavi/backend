package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotionTemplate_Version is a Querydsl query type for Version
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionTemplate_Version extends BeanPath<NotionTemplate.Version> {

    private static final long serialVersionUID = 1480452571L;

    public static final QNotionTemplate_Version version = new QNotionTemplate_Version("version");

    public final StringPath changelog = createString("changelog");

    public final NumberPath<Integer> major = createNumber("major", Integer.class);

    public final NumberPath<Integer> minor = createNumber("minor", Integer.class);

    public final NumberPath<Integer> patch = createNumber("patch", Integer.class);

    public QNotionTemplate_Version(String variable) {
        super(NotionTemplate.Version.class, forVariable(variable));
    }

    public QNotionTemplate_Version(Path<? extends NotionTemplate.Version> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionTemplate_Version(PathMetadata metadata) {
        super(NotionTemplate.Version.class, metadata);
    }

}

