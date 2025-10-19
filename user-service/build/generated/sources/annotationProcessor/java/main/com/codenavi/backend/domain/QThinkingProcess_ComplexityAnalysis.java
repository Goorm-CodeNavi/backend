package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QThinkingProcess_ComplexityAnalysis is a Querydsl query type for ComplexityAnalysis
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QThinkingProcess_ComplexityAnalysis extends BeanPath<ThinkingProcess.ComplexityAnalysis> {

    private static final long serialVersionUID = -1747188757L;

    public static final QThinkingProcess_ComplexityAnalysis complexityAnalysis = new QThinkingProcess_ComplexityAnalysis("complexityAnalysis");

    public final StringPath spaceComplexity = createString("spaceComplexity");

    public final StringPath timeComplexity = createString("timeComplexity");

    public QThinkingProcess_ComplexityAnalysis(String variable) {
        super(ThinkingProcess.ComplexityAnalysis.class, forVariable(variable));
    }

    public QThinkingProcess_ComplexityAnalysis(Path<? extends ThinkingProcess.ComplexityAnalysis> path) {
        super(path.getType(), path.getMetadata());
    }

    public QThinkingProcess_ComplexityAnalysis(PathMetadata metadata) {
        super(ThinkingProcess.ComplexityAnalysis.class, metadata);
    }

}

