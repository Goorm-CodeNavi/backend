package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hint_usage")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HintUsage {

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "hint_usage_requested_hints", joinColumns = @JoinColumn(name = "hint_usage_id"))
    private List<HintRequested> hintsRequested = new ArrayList<>();

    @Embedded
    private UsagePatterns usagePatterns;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class HintRequested {
        @Column(name = "hint_level")
        private Integer level;

        @Column(name = "hint_type")
        private String hintType;

        @Lob
        @Column(name = "hint_content")
        private String hintContent;

        @Column(name = "hint_requested_at")
        private LocalDateTime requestedAt;

        @Column(name = "hint_phase")
        private String phase;

        @Embedded
        private Effectiveness effectiveness;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Effectiveness {
        @Column(name = "hint_eff_user_rating")
        private Integer userRating;
        @Column(name = "hint_eff_led_to_progress")
        private Boolean ledToProgress;
        @Column(name = "hint_eff_time_to_progress")
        private Long timeToProgress;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UsagePatterns {
        @Column(name = "pattern_total_hints_used")
        private Integer totalHintsUsed;

        @Embedded
        private HintsByLevel hintsByLevel;

        @Embedded
        private HintsByPhase hintsByPhase;

        @Column(name = "pattern_hint_dependency_score")
        private Double hintDependencyScore;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class HintsByLevel {
        @Column(name = "pattern_level_1_count")
        private Integer level1;
        @Column(name = "pattern_level_2_count")
        private Integer level2;
        @Column(name = "pattern_level_3_count")
        private Integer level3;
        @Column(name = "pattern_level_4_count")
        private Integer level4;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class HintsByPhase {
        @Column(name = "pattern_phase_thinking_count")
        private Integer thinking;
        @Column(name = "pattern_phase_coding_count")
        private Integer coding;
        @Column(name = "pattern_phase_debugging_count")
        private Integer debugging;
    }
}

