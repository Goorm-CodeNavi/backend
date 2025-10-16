package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblemTag is a Querydsl query type for ProblemTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblemTag extends EntityPathBase<ProblemTag> {

    private static final long serialVersionUID = 780079005L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProblemTag problemTag = new QProblemTag("problemTag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProblem problem;

    public final QTag tag;

    public QProblemTag(String variable) {
        this(ProblemTag.class, forVariable(variable), INITS);
    }

    public QProblemTag(Path<? extends ProblemTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProblemTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProblemTag(PathMetadata metadata, PathInits inits) {
        this(ProblemTag.class, metadata, inits);
    }

    public QProblemTag(Class<? extends ProblemTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.problem = inits.isInitialized("problem") ? new QProblem(forProperty("problem"), inits.get("problem")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

