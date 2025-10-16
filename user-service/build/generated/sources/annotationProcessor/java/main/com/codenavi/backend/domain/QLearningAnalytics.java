package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLearningAnalytics is a Querydsl query type for LearningAnalytics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLearningAnalytics extends EntityPathBase<LearningAnalytics> {

    private static final long serialVersionUID = -2092041050L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLearningAnalytics learningAnalytics = new QLearningAnalytics("learningAnalytics");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final MapPath<String, Object, SimplePath<Object>> performanceMetrics = this.<String, Object, SimplePath<Object>>createMap("performanceMetrics", String.class, Object.class, SimplePath.class);

    public final DateTimePath<java.time.LocalDateTime> periodEnd = createDateTime("periodEnd", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> periodStart = createDateTime("periodStart", java.time.LocalDateTime.class);

    public final StringPath periodType = createString("periodType");

    public final MapPath<String, Object, SimplePath<Object>> progressTracking = this.<String, Object, SimplePath<Object>>createMap("progressTracking", String.class, Object.class, SimplePath.class);

    public final ListPath<java.util.Map<String, Object>, SimplePath<java.util.Map<String, Object>>> recommendations = this.<java.util.Map<String, Object>, SimplePath<java.util.Map<String, Object>>>createList("recommendations", java.util.Map.class, SimplePath.class, PathInits.DIRECT2);

    public final MapPath<String, Object, SimplePath<Object>> skillAnalysis = this.<String, Object, SimplePath<Object>>createMap("skillAnalysis", String.class, Object.class, SimplePath.class);

    public final MapPath<String, Object, SimplePath<Object>> timeAnalysis = this.<String, Object, SimplePath<Object>>createMap("timeAnalysis", String.class, Object.class, SimplePath.class);

    public final QUser user;

    public QLearningAnalytics(String variable) {
        this(LearningAnalytics.class, forVariable(variable), INITS);
    }

    public QLearningAnalytics(Path<? extends LearningAnalytics> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLearningAnalytics(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLearningAnalytics(PathMetadata metadata, PathInits inits) {
        this(LearningAnalytics.class, metadata, inits);
    }

    public QLearningAnalytics(Class<? extends LearningAnalytics> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

