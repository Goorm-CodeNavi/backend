package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser_Subscription is a Querydsl query type for Subscription
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUser_Subscription extends BeanPath<User.Subscription> {

    private static final long serialVersionUID = 366461214L;

    public static final QUser_Subscription subscription = new QUser_Subscription("subscription");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final StringPath plan = createString("plan");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public QUser_Subscription(String variable) {
        super(User.Subscription.class, forVariable(variable));
    }

    public QUser_Subscription(Path<? extends User.Subscription> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser_Subscription(PathMetadata metadata) {
        super(User.Subscription.class, metadata);
    }

}

