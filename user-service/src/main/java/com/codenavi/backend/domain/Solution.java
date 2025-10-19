package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

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
    private User user; // 제출한 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem; // 문제 번호

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자

    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ThinkingProcess thinkingProcess;

    @Embedded
    private Implementation implementation; // 언어, 실제 코드, 코드 작성 시간

    @Embedded
    private Status status; // 풀이 상태

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotionSyncRecord> notionSyncRecords = new ArrayList<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Implementation {
        @Column(name = "impl_language")
        private String language; // 언어

        @Lob
        @Column(name = "impl_code")
        private String code; // 코드

        @Column(name = "impl_time")
        private Long implementationTime; // 코드 작성 시간
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Status {
        @Column(name = "status_current")
        private String currentStatus; // 플이 상태

        @Column(name = "status_is_correct")
        private boolean isCorrect; // 정답 여부
    }

}

