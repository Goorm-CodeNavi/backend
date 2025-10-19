package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser_Statistics is a Querydsl query type for Statistics
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUser_Statistics extends BeanPath<User.Statistics> {

    private static final long serialVersionUID = -1989532316L;

    public static final QUser_Statistics statistics = new QUser_Statistics("statistics");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final NumberPath<Integer> problemsSolved = createNumber("problemsSolved", Integer.class);

    public final NumberPath<Integer> streakDays = createNumber("streakDays", Integer.class);

    public final NumberPath<Long> timeSpent = createNumber("timeSpent", Long.class);

    public final NumberPath<Integer> xp = createNumber("xp", Integer.class);

    public QUser_Statistics(String variable) {
        super(User.Statistics.class, forVariable(variable));
    }

    public QUser_Statistics(Path<? extends User.Statistics> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser_Statistics(PathMetadata metadata) {
        super(User.Statistics.class, metadata);
    }

}

