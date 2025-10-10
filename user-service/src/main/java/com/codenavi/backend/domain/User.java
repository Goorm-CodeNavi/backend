package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Embedded
    private Profile profile;

    @Embedded
    private Preferences preferences;

    @Embedded
    private Subscription subscription;

    @Embedded
    private Statistics statistics;

    @Embedded
    private AccountStatus accountStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(String username, String email , String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.ROLE_USER; // 회원가입 시 기본 역할 설정
        this.accountStatus = new AccountStatus(true, true); // 기본 계정 활성 상태 설정
    }

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solution> solutions = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private NotionConfig notionConfig;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningAnalytics> learningAnalytics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAchievement> userAchievements = new ArrayList<>();


    // --- UserDetails 구현 메소드 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 또는 accountStatus 필드를 활용
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 또는 accountStatus 필드를 활용
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 또는 accountStatus 필드를 활용
    }

    @Override
    public boolean isEnabled() {
        return this.accountStatus != null && this.accountStatus.isActive();
    }

    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Profile {
        @Column(name = "profile_first_name")
        private String firstName;

        @Column(name = "profile_last_name")
        private String lastName;

        @Column(name = "profile_avatar_url")
        private String avatarUrl;

        @Column(name = "profile_bio")
        private String bio;

        @Column(name = "profile_github_url")
        private String githubUrl;

        @Column(name = "profile_email")
        private String email;

        @Column(name = "profile_username")
        private String username;


    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Preferences {
        @Column(name = "pref_theme")
        private String theme;

        @Column(name = "pref_language")
        private String language;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "pref_notifications")
        private Map<String, Object> notifications;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "pref_editor_settings")
        private Map<String, Object> editorSettings;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Subscription {
        @Column(name = "sub_plan")
        private String plan;

        @Column(name = "sub_start_date")
        private LocalDate startDate;

        @Column(name = "sub_end_date")
        private LocalDate endDate;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Statistics {
        @Column(name = "stats_problems_solved")
        private Integer problemsSolved;

        @Column(name = "stats_time_spent")
        private Long timeSpent;

        @Column(name = "stats_streak_days")
        private Integer streakDays;

        @Column(name = "stats_level")
        private Integer level;

        @Column(name = "stats_xp")
        private Integer xp;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AccountStatus {
        @Column(name = "status_is_active")
        private boolean isActive;

        @Column(name = "status_is_verified")
        private boolean isVerified;

        public AccountStatus(boolean isActive, boolean isVerified) {
            this.isActive = isActive;
            this.isVerified = isVerified;
        }
    }
}
