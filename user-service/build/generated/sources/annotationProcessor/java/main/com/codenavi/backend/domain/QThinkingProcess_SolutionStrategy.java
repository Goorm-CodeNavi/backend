package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QThinkingProcess_SolutionStrategy is a Querydsl query type for SolutionStrategy
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QThinkingProcess_SolutionStrategy extends BeanPath<ThinkingProcess.SolutionStrategy> {

    private static final long serialVersionUID = 1515579709L;

    public static final QThinkingProcess_SolutionStrategy solutionStrategy = new QThinkingProcess_SolutionStrategy("solutionStrategy");

    public final StringPath algorithm = createString("algorithm");

    public final StringPath approachType = createString("approachType");

    public final StringPath content = createString("content");

    public QThinkingProcess_SolutionStrategy(String variable) {
        super(ThinkingProcess.SolutionStrategy.class, forVariable(variable));
    }

    public QThinkingProcess_SolutionStrategy(Path<? extends ThinkingProcess.SolutionStrategy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QThinkingProcess_SolutionStrategy(PathMetadata metadata) {
        super(ThinkingProcess.SolutionStrategy.class, metadata);
    }

}

