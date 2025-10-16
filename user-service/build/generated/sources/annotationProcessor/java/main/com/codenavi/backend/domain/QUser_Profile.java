package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser_Profile is a Querydsl query type for Profile
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUser_Profile extends BeanPath<User.Profile> {

    private static final long serialVersionUID = -1973787384L;

    public static final QUser_Profile profile = new QUser_Profile("profile");

    public final StringPath avatarUrl = createString("avatarUrl");

    public final StringPath bio = createString("bio");

    public final StringPath email = createString("email");

    public final StringPath firstName = createString("firstName");

    public final StringPath githubUrl = createString("githubUrl");

    public final StringPath lastName = createString("lastName");

    public final StringPath username = createString("username");

    public QUser_Profile(String variable) {
        super(User.Profile.class, forVariable(variable));
    }

    public QUser_Profile(Path<? extends User.Profile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser_Profile(PathMetadata metadata) {
        super(User.Profile.class, metadata);
    }

}

