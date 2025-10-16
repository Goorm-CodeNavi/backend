package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1055518829L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QUser_AccountStatus accountStatus;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<LearningAnalytics, QLearningAnalytics> learningAnalytics = this.<LearningAnalytics, QLearningAnalytics>createList("learningAnalytics", LearningAnalytics.class, QLearningAnalytics.class, PathInits.DIRECT2);

    public final QNotionConfig notionConfig;

    public final StringPath password = createString("password");

    public final QUser_Preferences preferences;

    public final QUser_Profile profile;

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final ListPath<Solution, QSolution> solutions = this.<Solution, QSolution>createList("solutions", Solution.class, QSolution.class, PathInits.DIRECT2);

    public final QUser_Statistics statistics;

    public final QUser_Subscription subscription;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final ListPath<UserAchievement, QUserAchievement> userAchievements = this.<UserAchievement, QUserAchievement>createList("userAchievements", UserAchievement.class, QUserAchievement.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.accountStatus = inits.isInitialized("accountStatus") ? new QUser_AccountStatus(forProperty("accountStatus")) : null;
        this.notionConfig = inits.isInitialized("notionConfig") ? new QNotionConfig(forProperty("notionConfig"), inits.get("notionConfig")) : null;
        this.preferences = inits.isInitialized("preferences") ? new QUser_Preferences(forProperty("preferences")) : null;
        this.profile = inits.isInitialized("profile") ? new QUser_Profile(forProperty("profile")) : null;
        this.statistics = inits.isInitialized("statistics") ? new QUser_Statistics(forProperty("statistics")) : null;
        this.subscription = inits.isInitialized("subscription") ? new QUser_Subscription(forProperty("subscription")) : null;
    }

}

