package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotionTemplate_Block is a Querydsl query type for Block
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionTemplate_Block extends BeanPath<NotionTemplate.Block> {

    private static final long serialVersionUID = -1817840048L;

    public static final QNotionTemplate_Block block = new QNotionTemplate_Block("block");

    public final StringPath content = createString("content");

    public final StringPath metadata = createString("metadata");

    public final StringPath type = createString("type");

    public QNotionTemplate_Block(String variable) {
        super(NotionTemplate.Block.class, forVariable(variable));
    }

    public QNotionTemplate_Block(Path<? extends NotionTemplate.Block> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionTemplate_Block(PathMetadata metadata) {
        super(NotionTemplate.Block.class, metadata);
    }

}

