package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ai_feedback")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    private Solution solution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    private ModelSolution modelSolution;

    @Embedded
    private ComparisonAnalysis comparisonAnalysis;

    @Embedded
    private DetailedFeedback detailedFeedback;

    @Embedded
    private AiMetadata aiMetadata;

    @Embedded
    private UserFeedback userFeedback;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ModelSolution {
        @Embedded
        private ThinkingProcess thinkingProcess;

        @Embedded
        private Implementation implementation;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "ms_alternative_solutions")
        private List<String> alternativeSolutions;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ThinkingProcess {
        @Lob
        @Column(name = "ms_tp_problem_summary")
        private String problemSummary;
        @Lob
        @Column(name = "ms_tp_solution_strategy")
        private String solutionStrategy;
        @Lob
        @Column(name = "ms_tp_complexity_analysis")
        private String complexityAnalysis;
        @Lob
        @Column(name = "ms_tp_pseudocode")
        private String pseudocode;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Implementation {
        @Lob
        @Column(name = "ms_impl_code")
        private String code;
        @Lob
        @Column(name = "ms_impl_explanation")
        private String explanation;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ComparisonAnalysis {
        @Embedded
        private ThinkingProcessComparison thinkingProcessComparison;
        @Embedded
        private CodeComparison codeComparison;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ThinkingProcessComparison {
        @Lob
        @Column(name = "ca_tp_problem_understanding")
        private String problemUnderstanding;
        @Lob
        @Column(name = "ca_tp_strategy_selection")
        private String strategySelection;
        @Lob
        @Column(name = "ca_tp_complexity_accuracy")
        private String complexityAccuracy;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CodeComparison {
        @Lob
        @Column(name = "ca_code_correctness")
        private String correctness;
        @Lob
        @Column(name = "ca_code_efficiency")
        private String efficiency;
        @Lob
        @Column(name = "ca_code_readability")
        private String readability;
        @Lob
        @Column(name = "ca_code_best_practices")
        private String bestPractices;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DetailedFeedback {
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "df_strengths")
        private List<String> strengths;
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "df_weaknesses")
        private List<String> weaknesses;
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "df_key_insights")
        private List<String> keyInsights;
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "df_next_steps")
        private List<String> nextSteps;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AiMetadata {
        @Column(name = "ai_model_version")
        private String modelVersion;
        @Column(name = "ai_confidence")
        private Double confidence;
        @Column(name = "ai_processing_time")
        private Long processingTime;
        @Column(name = "ai_tokens_used")
        private Integer tokensUsed;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserFeedback {
        @Column(name = "uf_helpfulness_rating")
        private Integer helpfulnessRating;
        @Column(name = "uf_accuracy_rating")
        private Integer accuracyRating;
        @Lob
        @Column(name = "uf_comments")
        private String comments;
    }
}

