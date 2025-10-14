package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notion_templates")
@Getter
@Setter
@NoArgsConstructor
public class NotionTemplate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "template_id", nullable = false, unique = true)
    private String templateId;

    @Column(name = "category")
    private String category;

    @Embedded
    private TemplateStructure templateStructure;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "notion_template_variable_mappings", joinColumns = @JoinColumn(name = "template_id"))
    private List<VariableMapping> variableMappings = new ArrayList<>();

    @Embedded
    private Metadata metadata;

    @Embedded
    private Version version;


    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TemplateStructure {
        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "notion_template_blocks", joinColumns = @JoinColumn(name = "template_id"))
        private List<Block> blocks = new ArrayList<>();

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "notion_template_properties", joinColumns = @JoinColumn(name = "template_id"))
        private List<Property> properties = new ArrayList<>();
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Block {
        @Column(name = "block_type")
        private String type;

        @Lob
        @Column(name = "block_content")
        private String content; // Store as JSON String

        @Lob
        @Column(name = "block_metadata")
        private String metadata; // Store as JSON String
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Property {
        @Column(name = "prop_name")
        private String name;

        @Column(name = "prop_type")
        private String type;

        @Lob
        @Column(name = "prop_options")
        private String options; // Store as JSON String
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class VariableMapping {
        @Column(name = "var_name")
        private String variableName;

        @Column(name = "var_data_path")
        private String dataPath; // JSONPath

        @Column(name = "var_transform_function")
        private String transformFunction;

        @Column(name = "var_default_value")
        private String defaultValue;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Metadata {
        @Lob
        @Column(name = "meta_description")
        private String description;

        @Column(name = "meta_author_id")
        private String authorId;

        @Column(name = "meta_is_official")
        private Boolean isOfficial;

        @Column(name = "meta_is_public")
        private Boolean isPublic;

        @Column(name = "meta_usage_count")
        private Integer usageCount;

        @Column(name = "meta_rating")
        private Double rating;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "meta_tags")
        private List<String> tags;

        @Column(name = "meta_preview_url")
        private String previewUrl;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Version {
        @Column(name = "ver_major")
        private Integer major;
        @Column(name = "ver_minor")
        private Integer minor;
        @Column(name = "ver_patch")
        private Integer patch;

        @Lob
        @Column(name = "ver_changelog")
        private String changelog;
    }
}
