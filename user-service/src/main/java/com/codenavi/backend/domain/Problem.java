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

    @Column(unique = true, nullable = false)
    private String number; // 문제 번호

    @Column(nullable = false)
    private String title; // 문제 제목

    @Embedded
    private Description description; // 문제 내용, 입력, 출력 설명

    @Embedded
    private Metadata metadata; // 카테고리

    @Embedded
    private Author author; // 출제자 정보

    @Embedded
    private Editorial editorial; //문제 요약, 해결 전략 및 접근법, 시간/공간 복잡도 분석, 의사코드

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 업데이트 일자

    // Relationships
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solution> solutions = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemTag> problemTags = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> testCases = new ArrayList<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Description {
        @Lob
        @Column(name = "desc_content")
        private String content; // 문제 설명 (핵심 내용)

        @Lob
        @Column(name = "desc_input")
        private String inputDescription; // 입력 설명 (제약조건 포함)

        @Lob
        @Column(name = "desc_output")
        private String outputDescription; // 출력 설명
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Metadata {
        @Column(name = "meta_category")
        private String category; // 카테고리
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Author {
        @Column(name = "author_user_id")
        private Long userId; // 출제자 id

        @Column(name = "author_name")
        private String name; // 출제자 이름

        @Column(name = "author_is_official")
        private boolean isOfficial; // 공식 여부
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Editorial {
        @Lob
        @Column(name = "ai_summary")
        private String summary; // AI가 생성한 '문제 요약'

        @Lob
        @Column(name = "ai_strategy")
        private String strategy; // AI가 생성한 '해결 전략 및 접근법'

        @Embedded
        private Complexity complexity; // AI가 분석한 '시간/공간 복잡도'

        @Lob
        @Column(name = "ai_pseudocode")
        private String pseudocode; // AI가 생성한 '의사코드'

        // AI의 모범 답안 코드도 추가할 수 있습니다.
//        @Lob
//        @Column(name = "ai_solution_code")
//        private String solutionCode;
    }


    // AI와 사용자의 복잡도 클래스는 재사용할 수 있습니다.
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Complexity {
        @Column(name = "comp_time")
        private String time;

        @Column(name = "comp_space")
        private String space;
    }
}