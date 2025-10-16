package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProblem_Complexity is a Querydsl query type for Complexity
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QProblem_Complexity extends BeanPath<Problem.Complexity> {

    private static final long serialVersionUID = 893036015L;

    public static final QProblem_Complexity complexity = new QProblem_Complexity("complexity");

    public final StringPath space = createString("space");

    public final StringPath time = createString("time");

    public QProblem_Complexity(String variable) {
        super(Problem.Complexity.class, forVariable(variable));
    }

    public QProblem_Complexity(Path<? extends Problem.Complexity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProblem_Complexity(PathMetadata metadata) {
        super(Problem.Complexity.class, metadata);
    }

}

