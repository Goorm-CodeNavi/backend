package com.codenavi.backend.dto;

import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.TestCase;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 문제 상세 조회 API의 응답 DTO 입니다.
 */
@Getter
@Builder
public class ProblemDetailDto {

    private String number;
    private String title;
    private String content;
    private String inputDescription;
    private String outputDescription;
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<ExampleDto> examples;

    @Getter
    @Builder
    public static class ExampleDto {
        private String input;
        private String output;
    }

    /**
     * Problem 엔티티를 API 응답에 필요한 DTO 형태로 변환합니다.
     * @param problem 조회된 Problem 엔티티 객체
     * @return 변환된 DTO 객체
     */
    public static ProblemDetailDto from(Problem problem) {
        // 공개된 테스트 케이스만 예시로 변환합니다.
        List<ExampleDto> publicExamples = problem.getTestCases().stream()
                .filter(TestCase::isPublic)
                .map(tc -> ExampleDto.builder()
                        .input(tc.getInput())
                        .output(tc.getExpectedOutput())
                        .build())
                .collect(Collectors.toList());

        // TestCase 중 첫 번째 공개 케이스의 제한 시간을 대표값으로 사용합니다.
        Integer representativeTimeLimit = problem.getTestCases().stream()
                .filter(TestCase::isPublic).map(TestCase::getTimeLimit).findFirst().orElse(1000);
        Integer representativeMemoryLimit = problem.getTestCases().stream()
                .filter(TestCase::isPublic).map(TestCase::getMemoryLimit).findFirst().orElse(512);

        return ProblemDetailDto.builder()
                .number(problem.getNumber())
                .title(problem.getTitle())
                .content(problem.getDescription().getContent())
                .inputDescription(problem.getDescription().getInputDescription())
                .outputDescription(problem.getDescription().getOutputDescription())
                .timeLimit(representativeTimeLimit)
                .memoryLimit(representativeMemoryLimit)
                .examples(publicExamples)
                .build();
    }
}
