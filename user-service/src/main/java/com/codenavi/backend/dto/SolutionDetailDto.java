package com.codenavi.backend.dto;

import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.domain.ThinkingProcess;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상세 제출 기록 조회 API의 응답 DTO 입니다.
 */
@Getter
@Builder
public class SolutionDetailDto {

    private ProblemInfo problemInfo;
    private ThinkingProcessDto thinkingProcess;
    private ImplementationDto implementation;
    private StatusInfoDto statusInfo;

    // --- API 응답의 각 JSON 객체에 해당하는 중첩 DTO 클래스들 ---

    @Getter
    @Builder
    public static class ProblemInfo {
        private String number;
        private String title;
    }

    @Getter
    @Builder
    public static class ThinkingProcessDto {
        private String problemSummary;
        private String solutionStrategy;
        private ComplexityAnalysisDto complexityAnalysis;
        private String pseudocode;
    }

    @Getter
    @Builder
    public static class ComplexityAnalysisDto {
        private String timeAndSpace;
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
    }

    /**
     * Solution 엔티티와 연관된 모든 정보를 DTO로 변환하는 정적 팩토리 메소드입니다.
     * @param solution 조회된 Solution 엔티티 (Problem, ThinkingProcess를 포함해야 함)
     * @return 변환된 DTO 객체
     */
    public static SolutionDetailDto from(Solution solution) {
        ThinkingProcess tp = solution.getThinkingProcess();

        // 사고 과정(ThinkingProcess)이 아직 작성되지 않은 경우를 대비하여 null 체크를 합니다.
        ThinkingProcessDto tpDto = (tp != null) ?
                ThinkingProcessDto.builder()
                        .problemSummary(tp.getProblemSummary() != null ? tp.getProblemSummary().getContent() : null)
                        .solutionStrategy(tp.getSolutionStrategy() != null ? tp.getSolutionStrategy().getContent() : null)
                        .complexityAnalysis(
                                (tp.getComplexityAnalysis() != null) ?
                                        ComplexityAnalysisDto.builder()
                                                .timeAndSpace(tp.getComplexityAnalysis().getTimeComplexity())
                                                .build() : null
                        )
                        .pseudocode(tp.getPseudocode() != null ? tp.getPseudocode().getContent() : null)
                        .build() : null;

        return SolutionDetailDto.builder()
                .problemInfo(
                        ProblemInfo.builder()
                                .number(solution.getProblem().getNumber())
                                .title(solution.getProblem().getTitle())
                                .build()
                )
                .thinkingProcess(tpDto)
                .implementation(
                        ImplementationDto.builder()
                                .language(solution.getImplementation().getLanguage())
                                .code(solution.getImplementation().getCode())
                                .build()
                )
                .statusInfo(
                        StatusInfoDto.builder()
                                .status(solution.getStatus().isCorrect() ? "정답" : "오답")
                                .submittedAt(solution.getCreatedAt())
                                .build()
                )
                .build();
    }
}

