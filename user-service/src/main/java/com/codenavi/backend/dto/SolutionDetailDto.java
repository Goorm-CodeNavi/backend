package com.codenavi.backend.dto;

import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.domain.TestCase;
import com.codenavi.backend.domain.ThinkingProcess;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상세 제출 기록 조회 API의 응답 DTO 입니다.
 */
@Getter
@Builder
public class SolutionDetailDto {

    private ProblemInfo problemInfo;
    private AiEditorialDto aiSolution;
    private ThinkingProcessDto userThinkingProcess;
    private ImplementationDto userImplementation;
    private StatusInfoDto statusInfo;

    // --- API 응답의 각 JSON 객체에 해당하는 중첩 DTO 클래스들 ---

    @Getter
    @Builder
    public static class ProblemInfo {
        private String number;
        private String title;
        private String content;
        private String inputDescription;
        private String outputDescription;
        private Integer timeLimit;
        private Integer memoryLimit;
        private List<ExampleDto> examples;
    }

    @Getter
    @Builder
    public static class ExampleDto { // ProblemInfo 내부에서 사용될 예시 DTO
        private String input;
        private String output;
    }

    @Getter
    @Builder
    public static class AiEditorialDto {
        private String summary;
        private String strategy;
        private String complexity;
        private String pseudocode;
    }

    @Getter
    @Builder
    public static class ThinkingProcessDto {
        private String problemSummary;
        private String solutionStrategy;
        private ComplexityDto complexityAnalysis;
        private String pseudocode; // Pseudocode 필드 추가
    }

    @Getter
    @Builder
    public static class ComplexityDto {
        private String time;
        private String space;
    }

    @Getter
    @Builder
    public static class ImplementationDto {
        private String language;
        private String code;
    }

    @Getter
    @Builder
    public static class StatusInfoDto {
        private String status;
        private LocalDateTime submittedAt;
        private Long timeSpent;
    }

    /**
     * Solution 엔티티와 연관된 모든 정보를 DTO로 변환합니다.
     * @param solution 조회된 Solution 엔티티 (Problem, ThinkingProcess 포함)
     * @return 변환된 DTO 객체
     */
    public static SolutionDetailDto from(Solution solution) {
        Problem problem = solution.getProblem();
        ThinkingProcess tp = solution.getThinkingProcess();

        List<ExampleDto> publicExamples = problem.getTestCases().stream()
                .filter(TestCase::isPublic)
                .map(tc -> ExampleDto.builder()
                        .input(tc.getInput())
                        .output(tc.getExpectedOutput())
                        .build())
                .collect(Collectors.toList());

        Integer representativeTimeLimit = problem.getTestCases().stream()
                .filter(TestCase::isPublic).map(TestCase::getTimeLimit).findFirst().orElse(1000);
        Integer representativeMemoryLimit = problem.getTestCases().stream()
                .filter(TestCase::isPublic).map(TestCase::getMemoryLimit).findFirst().orElse(512);

        // --- 👇 수정된 부분: @Embedded 구조에 맞게 getter를 사용합니다. ---
        AiEditorialDto aiDto = AiEditorialDto.builder()
                .summary(problem.getEditorial().getSummary())
                .strategy(problem.getEditorial().getStrategy())
                .complexity(problem.getEditorial().getComplexity().getTimeAndSpace()) // Complexity의 timeAndSpace 필드 사용
                .pseudocode(problem.getEditorial().getPseudocode())
                .build();
        // -----------------------------------------------------------

        ThinkingProcessDto tpDto = (tp != null) ?
                ThinkingProcessDto.builder()
                        .problemSummary(tp.getProblemSummary() != null ? tp.getProblemSummary().getContent() : null)
                        .solutionStrategy(tp.getSolutionStrategy() != null ? tp.getSolutionStrategy().getContent() : null)
                        .complexityAnalysis(
                                (tp.getComplexityAnalysis() != null) ?
                                        ComplexityDto.builder()
                                                .time(tp.getComplexityAnalysis().getTimeComplexity()) // ThinkingProcess의 필드 사용
                                                .space(tp.getComplexityAnalysis().getSpaceComplexity()) // ThinkingProcess의 필드 사용
                                                .build() : null
                        )
                        .pseudocode(tp.getPseudocode() != null ? tp.getPseudocode().getContent() : null)
                        .build() : null;

        return SolutionDetailDto.builder()
                .problemInfo(
                        ProblemInfo.builder()
                                .number(problem.getNumber())
                                .title(problem.getTitle())
                                // --- 👇 수정된 부분: @Embedded 구조에 맞게 getter를 사용합니다. ---
                                .content(problem.getDescription().getContent())
                                .inputDescription(problem.getDescription().getInputDescription())
                                .outputDescription(problem.getDescription().getOutputDescription())
                                // -----------------------------------------------------------
                                .timeLimit(representativeTimeLimit)
                                .memoryLimit(representativeMemoryLimit)
                                .examples(publicExamples)
                                .build()
                )
                .aiSolution(aiDto)
                .userThinkingProcess(tpDto)
                .userImplementation(ImplementationDto.builder()
                        .language(solution.getImplementation().getLanguage())
                        .code(solution.getImplementation().getCode())
                        .build())
                .statusInfo(StatusInfoDto.builder()
                        .status(solution.getStatus().isCorrect() ? "정답" : "오답")
                        .submittedAt(solution.getCreatedAt())
                        .timeSpent(solution.getImplementation().getImplementationTime())
                        .build())
                .build();
    }
}

