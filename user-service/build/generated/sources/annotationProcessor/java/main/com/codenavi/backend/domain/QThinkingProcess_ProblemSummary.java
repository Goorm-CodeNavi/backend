package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QThinkingProcess_ProblemSummary is a Querydsl query type for ProblemSummary
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QThinkingProcess_ProblemSummary extends BeanPath<ThinkingProcess.ProblemSummary> {

    private static final long serialVersionUID = 1550438776L;

    public static final QThinkingProcess_ProblemSummary problemSummary = new QThinkingProcess_ProblemSummary("problemSummary");

    public final DateTimePath<java.time.LocalDateTime> completedAt = createDateTime("completedAt", java.time.LocalDateTime.class);

    public final StringPath content = createString("content");

    public final NumberPath<Long> timeSpent = createNumber("timeSpent", Long.class);

    public QThinkingProcess_ProblemSummary(String variable) {
        super(ThinkingProcess.ProblemSummary.class, forVariable(variable));
    }

    public QThinkingProcess_ProblemSummary(Path<? extends ThinkingProcess.ProblemSummary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QThinkingProcess_ProblemSummary(PathMetadata metadata) {
        super(ThinkingProcess.ProblemSummary.class, metadata);
    }

}

