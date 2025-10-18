package com.codenavi.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 코드 제출 API를 위한 요청 및 응답 DTO 입니다.
 */
public class CodeSubmissionDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        @NotBlank(message = "언어는 필수 입력값입니다.")
        private String language;

        @NotBlank(message = "코드는 필수 입력값입니다.")
        private String code;

        // 사고 과정 필드
        private String problemSummary;
        private String solutionStrategy;
        @Valid
        private ThinkingCanvasDto.ComplexityDto complexityAnalysis;
        private String pseudocode;

        @NotNull(message = "풀이 시간은 필수 입력값입니다.")
        private Long timeSpent; // 프론트에서 받은 측정 시간 (ms 단위)
    }

    @Getter
    @Builder
    public static class Response {
        private Long solutionId;
        private String status;
        private Integer failedCaseNumber;
        private Double executionTime;
        private Double memoryUsed;
    }
}

