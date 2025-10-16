package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblem_Editorial is a Querydsl query type for Editorial
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QProblem_Editorial extends BeanPath<Problem.Editorial> {

    private static final long serialVersionUID = 134623158L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProblem_Editorial editorial = new QProblem_Editorial("editorial");

    public final QProblem_Complexity complexity;

    public final StringPath pseudocode = createString("pseudocode");

    public final StringPath strategy = createString("strategy");

    public final StringPath summary = createString("summary");

    public QProblem_Editorial(String variable) {
        this(Problem.Editorial.class, forVariable(variable), INITS);
    }

    public QProblem_Editorial(Path<? extends Problem.Editorial> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProblem_Editorial(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProblem_Editorial(PathMetadata metadata, PathInits inits) {
        this(Problem.Editorial.class, metadata, inits);
    }

    public QProblem_Editorial(Class<? extends Problem.Editorial> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.complexity = inits.isInitialized("complexity") ? new QProblem_Complexity(forProperty("complexity")) : null;
    }

}

