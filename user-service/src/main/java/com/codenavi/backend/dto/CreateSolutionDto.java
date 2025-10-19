package com.codenavi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 풀이 생성 API를 위한 요청 및 응답 DTO 입니다.
 */
public class CreateSolutionDto {

    /**
     * 요청 DTO (기존 ThinkingCanvasDto.Request와 동일한 구조)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private String problemSummary;
        private String solutionStrategy;
        private ThinkingCanvasDto.ComplexityDto complexityAnalysis;
        private String pseudocode;
    }

    /**
     * 응답 DTO (생성된 solutionId 반환)
     */
    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long solutionId;
    }
}
