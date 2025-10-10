package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @Embedded
    private Description description;

    @Embedded
    private Metadata metadata;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "problem_test_cases", joinColumns = @JoinColumn(name = "problem_id"))
    private List<TestCase> testCases = new ArrayList<>();

    @Embedded
    private Statistics statistics;

    @Embedded
    private Author author;

    @Embedded
    private Editorial editorial;

    @Embedded
    private Status status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solution> solutions = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemTag> problemTags = new ArrayList<>();


    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Description {
        @Lob
        @Column(name = "desc_content")
        private String content; // Markdown

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "desc_examples")
        private List<String> examples;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "desc_constraints")
        private List<String> constraints;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Metadata {
        @Column(name = "meta_difficulty")
        private String difficulty;

        @Column(name = "meta_category")
        private String category;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "meta_tags")
        private List<String> tags;

        @Column(name = "meta_estimated_time")
        private Integer estimatedTime;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TestCase {
        @Lob
        @Column(name = "case_input")
        private String input;

        @Lob
        @Column(name = "case_expected_output")
        private String expectedOutput;

        @Column(name = "case_is_public")
        private boolean isPublic;

        @Column(name = "case_time_limit")
        private Integer timeLimit;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Statistics {
        @Column(name = "stats_total_submissions")
        private Integer totalSubmissions;

        @Column(name = "stats_acceptance_rate")
        private Double acceptanceRate;

        @Column(name = "stats_average_time_spent")
        private Long averageTimeSpent;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Author {
        @Column(name = "author_user_id")
        private Long userId;

        @Column(name = "author_name")
        private String name;

        @Column(name = "author_is_official")
        private boolean isOfficial;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Editorial {
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "edit_approaches")
        private List<String> approaches;

        @Lob
        @Column(name = "edit_explanations")
        private String explanations;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Status {
        @Column(name = "status_is_published")
        private boolean isPublished;

        @Column(name = "status_review_status")
        private String reviewStatus;
    }
}

