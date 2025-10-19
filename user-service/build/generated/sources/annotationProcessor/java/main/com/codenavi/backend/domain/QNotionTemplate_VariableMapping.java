package com.codenavi.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotionTemplate_VariableMapping is a Querydsl query type for VariableMapping
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotionTemplate_VariableMapping extends BeanPath<NotionTemplate.VariableMapping> {

    private static final long serialVersionUID = 1878684853L;

    public static final QNotionTemplate_VariableMapping variableMapping = new QNotionTemplate_VariableMapping("variableMapping");

    public final StringPath dataPath = createString("dataPath");

    public final StringPath defaultValue = createString("defaultValue");

    public final StringPath transformFunction = createString("transformFunction");

    public final StringPath variableName = createString("variableName");

    public QNotionTemplate_VariableMapping(String variable) {
        super(NotionTemplate.VariableMapping.class, forVariable(variable));
    }

    public QNotionTemplate_VariableMapping(Path<? extends NotionTemplate.VariableMapping> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotionTemplate_VariableMapping(PathMetadata metadata) {
        super(NotionTemplate.VariableMapping.class, metadata);
    }

}

