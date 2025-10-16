package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTestCase is a Querydsl query type for TestCase
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestCase extends EntityPathBase<TestCase> {

    private static final long serialVersionUID = -1441169788L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTestCase testCase = new QTestCase("testCase");

    public final StringPath expectedOutput = createString("expectedOutput");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath input = createString("input");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final NumberPath<Integer> memoryLimit = createNumber("memoryLimit", Integer.class);

    public final QProblem problem;

    public final NumberPath<Integer> timeLimit = createNumber("timeLimit", Integer.class);

    public QTestCase(String variable) {
        this(TestCase.class, forVariable(variable), INITS);
    }

    public QTestCase(Path<? extends TestCase> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTestCase(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTestCase(PathMetadata metadata, PathInits inits) {
        this(TestCase.class, metadata, inits);
    }

    public QTestCase(Class<? extends TestCase> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.problem = inits.isInitialized("problem") ? new QProblem(forProperty("problem"), inits.get("problem")) : null;
    }

}

