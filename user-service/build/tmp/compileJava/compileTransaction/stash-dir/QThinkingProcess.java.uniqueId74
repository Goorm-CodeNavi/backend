package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QThinkingProcess is a Querydsl query type for ThinkingProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QThinkingProcess extends EntityPathBase<ThinkingProcess> {

    private static final long serialVersionUID = 595754173L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QThinkingProcess thinkingProcess = new QThinkingProcess("thinkingProcess");

    public final QThinkingProcess_ComplexityAnalysis complexityAnalysis;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QThinkingProcess_ProblemSummary problemSummary;

    public final QThinkingProcess_Pseudocode pseudocode;

    public final QSolution solution;

    public final QThinkingProcess_SolutionStrategy solutionStrategy;

    public QThinkingProcess(String variable) {
        this(ThinkingProcess.class, forVariable(variable), INITS);
    }

    public QThinkingProcess(Path<? extends ThinkingProcess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QThinkingProcess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QThinkingProcess(PathMetadata metadata, PathInits inits) {
        this(ThinkingProcess.class, metadata, inits);
    }

    public QThinkingProcess(Class<? extends ThinkingProcess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.complexityAnalysis = inits.isInitialized("complexityAnalysis") ? new QThinkingProcess_ComplexityAnalysis(forProperty("complexityAnalysis")) : null;
        this.problemSummary = inits.isInitialized("problemSummary") ? new QThinkingProcess_ProblemSummary(forProperty("problemSummary")) : null;
        this.pseudocode = inits.isInitialized("pseudocode") ? new QThinkingProcess_Pseudocode(forProperty("pseudocode")) : null;
        this.solution = inits.isInitialized("solution") ? new QSolution(forProperty("solution"), inits.get("solution")) : null;
        this.solutionStrategy = inits.isInitialized("solutionStrategy") ? new QThinkingProcess_SolutionStrategy(forProperty("solutionStrategy")) : null;
    }

}

