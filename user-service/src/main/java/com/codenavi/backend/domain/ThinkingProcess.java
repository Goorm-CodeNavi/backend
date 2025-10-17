package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "thinking_processes")
public class ThinkingProcess {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Solution의 ID를 자신의 ID로 사용
    @JoinColumn(name = "solution_id")
    private Solution solution;

    @Embedded
    private ProblemSummary problemSummary; // 문제 요약
    @Embedded
    private SolutionStrategy solutionStrategy; // 해결 전략 및 접근법
    @Embedded
    private ComplexityAnalysis complexityAnalysis; // 시공간 복잡도 분석
    @Embedded
    private Pseudocode pseudocode; // 의사코드

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
}
