package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotionTemplate_Metadata is a Querydsl query type for Metadata
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionTemplate_Metadata extends BeanPath<NotionTemplate.Metadata> {

    private static final long serialVersionUID = 184438412L;

    public static final QNotionTemplate_Metadata metadata = new QNotionTemplate_Metadata("metadata");

    public final StringPath authorId = createString("authorId");

    public final StringPath description = createString("description");

    public final BooleanPath isOfficial = createBoolean("isOfficial");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final StringPath previewUrl = createString("previewUrl");

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final ListPath<String, StringPath> tags = this.<String, StringPath>createList("tags", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> usageCount = createNumber("usageCount", Integer.class);

    public QNotionTemplate_Metadata(String variable) {
        super(NotionTemplate.Metadata.class, forVariable(variable));
    }

    public QNotionTemplate_Metadata(Path<? extends NotionTemplate.Metadata> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionTemplate_Metadata(PathMetadata metadata) {
        super(NotionTemplate.Metadata.class, metadata);
    }

}

