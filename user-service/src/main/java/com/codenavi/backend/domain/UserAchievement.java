package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "user_achievements")
@Getter
@Setter
@NoArgsConstructor
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "achievement_type")
    private String achievementType;

    @Column(name = "achievement_id")
    private String achievementId;

    private String title;
    private String description;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
}
