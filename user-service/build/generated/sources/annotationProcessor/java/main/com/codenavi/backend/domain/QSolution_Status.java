package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolution_Status is a Querydsl query type for Status
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QSolution_Status extends BeanPath<Solution.Status> {

    private static final long serialVersionUID = 1683934277L;

    public static final QSolution_Status status = new QSolution_Status("status");

    public final StringPath currentStatus = createString("currentStatus");

    public final BooleanPath isCorrect = createBoolean("isCorrect");

    public QSolution_Status(String variable) {
        super(Solution.Status.class, forVariable(variable));
    }

    public QSolution_Status(Path<? extends Solution.Status> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolution_Status(PathMetadata metadata) {
        super(Solution.Status.class, metadata);
    }

}

