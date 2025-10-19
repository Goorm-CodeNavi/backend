package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblem is a Querydsl query type for Problem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblem extends EntityPathBase<Problem> {

    private static final long serialVersionUID = 1343546045L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProblem problem = new QProblem("problem");

    public final QProblem_Author author;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QProblem_Description description;

    public final QProblem_Editorial editorial;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProblem_Metadata metadata;

    public final StringPath number = createString("number");

    public final ListPath<ProblemTag, QProblemTag> problemTags = this.<ProblemTag, QProblemTag>createList("problemTags", ProblemTag.class, QProblemTag.class, PathInits.DIRECT2);

    public final ListPath<Solution, QSolution> solutions = this.<Solution, QSolution>createList("solutions", Solution.class, QSolution.class, PathInits.DIRECT2);

    public final ListPath<TestCase, QTestCase> testCases = this.<TestCase, QTestCase>createList("testCases", TestCase.class, QTestCase.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QProblem(String variable) {
        this(Problem.class, forVariable(variable), INITS);
    }

    public QProblem(Path<? extends Problem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProblem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProblem(PathMetadata metadata, PathInits inits) {
        this(Problem.class, metadata, inits);
    }

    public QProblem(Class<? extends Problem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QProblem_Author(forProperty("author")) : null;
        this.description = inits.isInitialized("description") ? new QProblem_Description(forProperty("description")) : null;
        this.editorial = inits.isInitialized("editorial") ? new QProblem_Editorial(forProperty("editorial"), inits.get("editorial")) : null;
        this.metadata = inits.isInitialized("metadata") ? new QProblem_Metadata(forProperty("metadata")) : null;
    }

}

