package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolution_Implementation is a Querydsl query type for Implementation
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QSolution_Implementation extends BeanPath<Solution.Implementation> {

    private static final long serialVersionUID = -581262075L;

    public static final QSolution_Implementation implementation = new QSolution_Implementation("implementation");

    public final StringPath code = createString("code");

    public final NumberPath<Long> implementationTime = createNumber("implementationTime", Long.class);

    public final StringPath language = createString("language");

    public QSolution_Implementation(String variable) {
        super(Solution.Implementation.class, forVariable(variable));
    }

    public QSolution_Implementation(Path<? extends Solution.Implementation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolution_Implementation(PathMetadata metadata) {
        super(Solution.Implementation.class, metadata);
    }

}

