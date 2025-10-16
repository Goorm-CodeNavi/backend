package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAchievement is a Querydsl query type for UserAchievement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAchievement extends EntityPathBase<UserAchievement> {

    private static final long serialVersionUID = 582463618L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAchievement userAchievement = new QUserAchievement("userAchievement");

    public final StringPath achievementId = createString("achievementId");

    public final StringPath achievementType = createString("achievementType");

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> earnedAt = createDateTime("earnedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final MapPath<String, Object, SimplePath<Object>> metadata = this.<String, Object, SimplePath<Object>>createMap("metadata", String.class, Object.class, SimplePath.class);

    public final StringPath title = createString("title");

    public final QUser user;

    public QUserAchievement(String variable) {
        this(UserAchievement.class, forVariable(variable), INITS);
    }

    public QUserAchievement(Path<? extends UserAchievement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAchievement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAchievement(PathMetadata metadata, PathInits inits) {
        this(UserAchievement.class, metadata, inits);
    }

    public QUserAchievement(Class<? extends UserAchievement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

