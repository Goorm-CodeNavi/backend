package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QThinkingProcess_Pseudocode is a Querydsl query type for Pseudocode
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QThinkingProcess_Pseudocode extends BeanPath<ThinkingProcess.Pseudocode> {

    private static final long serialVersionUID = 1044887196L;

    public static final QThinkingProcess_Pseudocode pseudocode = new QThinkingProcess_Pseudocode("pseudocode");

    public final DateTimePath<java.time.LocalDateTime> completedAt = createDateTime("completedAt", java.time.LocalDateTime.class);

    public final StringPath content = createString("content");

    public QThinkingProcess_Pseudocode(String variable) {
        super(ThinkingProcess.Pseudocode.class, forVariable(variable));
    }

    public QThinkingProcess_Pseudocode(Path<? extends ThinkingProcess.Pseudocode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QThinkingProcess_Pseudocode(PathMetadata metadata) {
        super(ThinkingProcess.Pseudocode.class, metadata);
    }

}

