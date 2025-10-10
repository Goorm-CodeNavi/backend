package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "solutions")
@Getter
@Setter
@NoArgsConstructor
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Embedded
    private ThinkingProcess thinkingProcess;

    @Embedded
    private Implementation implementation;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "solution_execution_results", joinColumns = @JoinColumn(name = "solution_id"))
    private List<ExecutionResult> executionResults = new ArrayList<>();

    @Embedded
    private Status status;

    @Embedded
    private Metadata metadata;

    // Relationships
    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TimeTracking timeTracking;

    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AiFeedback aiFeedback;

    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private HintUsage hintUsage;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotionSyncRecord> notionSyncRecords = new ArrayList<>();


    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ThinkingProcess {
        @Embedded
        private ProblemSummary problemSummary;
        @Embedded
        private SolutionStrategy solutionStrategy;
        @Embedded
        private ComplexityAnalysis complexityAnalysis;
        @Embedded
        private Pseudocode pseudocode;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProblemSummary {
        @Lob
        @Column(name = "tp_summary_content")
        private String content;

        @Column(name = "tp_summary_completed_at")
        private LocalDateTime completedAt;

        @Column(name = "tp_summary_time_spent")
        private Long timeSpent;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SolutionStrategy {
        @Lob
        @Column(name = "tp_strategy_content")
        private String content;

        @Column(name = "tp_strategy_algorithm")
        private String algorithm;

        @Column(name = "tp_strategy_approach_type")
        private String approachType;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ComplexityAnalysis {
        @Column(name = "tp_complexity_time")
        private String timeComplexity;

        @Column(name = "tp_complexity_space")
        private String spaceComplexity;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Pseudocode {
        @Lob
        @Column(name = "tp_pseudo_content")
        private String content;

        @Column(name = "tp_pseudo_completed_at")
        private LocalDateTime completedAt;
    }


    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Implementation {
        @Column(name = "impl_language")
        private String language;

        @Lob
        @Column(name = "impl_code")
        private String code;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "impl_code_versions")
        private List<String> codeVersions;

        @Column(name = "impl_time")
        private Long implementationTime;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ExecutionResult {
        @Column(name = "exec_test_case_id")
        private String testCaseId;

        @Column(name = "exec_status")
        private String status;

        @Column(name = "exec_time")
        private Long executionTime;

        @Column(name = "exec_memory_used")
        private Long memoryUsed;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Status {
        @Column(name = "status_current")
        private String currentStatus;

        @Column(name = "status_is_correct")
        private boolean isCorrect;

        @Column(name = "status_completion_time")
        private Long completionTime;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "status_scores")
        private Map<String, Object> scores;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Metadata {
        @Column(name = "meta_started_at")
        private LocalDateTime startedAt;

        @Column(name = "meta_completed_at")
        private LocalDateTime completedAt;

        @Column(name = "meta_session_id")
        private String sessionId;
    }
}

