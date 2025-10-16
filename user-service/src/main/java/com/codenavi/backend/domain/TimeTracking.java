//package com.codenavi.backend.domain;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "time_tracking")
//@Getter
//@Setter
//@NoArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
//public class TimeTracking {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "_id")
//    private Long id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "solution_id", nullable = false)
//    private Solution solution;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Embedded
//    private Phases phases;
//
//    @Column(name = "total_time")
//    private Long totalTime;
//
//    @Column(name = "active_time")
//    private Long activeTime;
//
//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(name = "time_tracking_pause_events", joinColumns = @JoinColumn(name = "time_tracking_id"))
//    private List<PauseEvent> pauseEvents = new ArrayList<>();
//
//    @Embedded
//    private AverageComparison averageComparison;
//
//    @CreatedDate
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//
//    // --- Inner Embeddable Classes ---
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class Phases {
//        @Embedded
//        private ReadingPhase reading;
//        @Embedded
//        private ThinkingPhase thinking;
//        @Embedded
//        private ImplementationPhase implementation;
//        @Embedded
//        private TestingPhase testing;
//    }
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class ReadingPhase {
//        @Column(name = "phase_read_start_time")
//        private LocalDateTime startTime;
//        @Column(name = "phase_read_end_time")
//        private LocalDateTime endTime;
//        @Column(name = "phase_read_duration")
//        private Long duration;
//        @Column(name = "phase_read_pause_duration")
//        private Long pauseDuration;
//    }
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class ThinkingPhase {
//        @Column(name = "phase_think_duration")
//        private Long duration;
//        @Embedded
//        private StepBreakdown stepBreakdown;
//    }
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class StepBreakdown {
//        @Column(name = "phase_think_sb_summary")
//        private Long problemSummary;
//        @Column(name = "phase_think_sb_strategy")
//        private Long solutionStrategy;
//        @Column(name = "phase_think_sb_complexity")
//        private Long complexityAnalysis;
//        @Column(name = "phase_think_sb_pseudocode")
//        private Long pseudocode;
//    }
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class ImplementationPhase {
//        @Column(name = "phase_impl_duration")
//        private Long duration;
//        @Column(name = "phase_impl_typing_time")
//        private Long typingTime;
//        @Column(name = "phase_impl_char_count")
//        private Integer characterCount;
//    }
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class TestingPhase {
//        @Column(name = "phase_test_duration")
//        private Long duration;
//        @Column(name = "phase_test_runs")
//        private Integer testRuns;
//        @Column(name = "phase_test_debug_iterations")
//        private Integer debugIterations;
//    }
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class PauseEvent {
//        @Column(name = "pause_start_time")
//        private LocalDateTime startTime;
//        @Column(name = "pause_end_time")
//        private LocalDateTime endTime;
//        @Column(name = "pause_duration")
//        private Long duration;
//        @Column(name = "pause_reason")
//        private String reason;
//    }
//
//    @Embeddable
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    public static class AverageComparison {
//        @Column(name = "comp_vs_global_avg")
//        private Double vsGlobalAverage;
//        @Column(name = "comp_vs_similar_level")
//        private Double vsSimilarLevel;
//        @Column(name = "comp_vs_personal_best")
//        private Double vsPersonalBest;
//    }
//}
//
