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
import java.util.Map;

@Entity
@Table(name = "learning_analytics")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LearningAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "period_type")
    private String periodType;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "performance_metrics")
    private Map<String, Object> performanceMetrics;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skill_analysis")
    private Map<String, Object> skillAnalysis;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "time_analysis")
    private Map<String, Object> timeAnalysis;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "progress_tracking")
    private Map<String, Object> progressTracking;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> recommendations;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
