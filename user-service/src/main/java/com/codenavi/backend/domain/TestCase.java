package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test_cases")
@Getter
@Setter
@NoArgsConstructor
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 독립적인 ID 부여

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Lob
    private String input;// 입력값

    @Lob
    private String expectedOutput;// 기대 결과값

    private boolean isPublic;// 테스트케이스 공개 여부

    private Integer timeLimit;// 시간 제한 (ms)

    private Integer memoryLimit;// 메모리 제한 (MB)
}
