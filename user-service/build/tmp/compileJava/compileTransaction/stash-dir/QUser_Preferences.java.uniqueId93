package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser_Preferences is a Querydsl query type for Preferences
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUser_Preferences extends BeanPath<User.Preferences> {

    private static final long serialVersionUID = -918818089L;

    public static final QUser_Preferences preferences = new QUser_Preferences("preferences");

    public final MapPath<String, Object, SimplePath<Object>> editorSettings = this.<String, Object, SimplePath<Object>>createMap("editorSettings", String.class, Object.class, SimplePath.class);

    public final StringPath language = createString("language");

    public final MapPath<String, Object, SimplePath<Object>> notifications = this.<String, Object, SimplePath<Object>>createMap("notifications", String.class, Object.class, SimplePath.class);

    public final StringPath theme = createString("theme");

    public QUser_Preferences(String variable) {
        super(User.Preferences.class, forVariable(variable));
    }

    public QUser_Preferences(Path<? extends User.Preferences> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser_Preferences(PathMetadata metadata) {
        super(User.Preferences.class, metadata);
    }

}

