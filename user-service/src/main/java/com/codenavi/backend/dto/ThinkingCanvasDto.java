package com.codenavi.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사고 과정 캔버스 저장 API의 RequestBody DTO 입니다.
 */
public class ThinkingCanvasDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private String problemSummary;
        private String solutionStrategy;
        private ComplexityDto complexityAnalysis;
        private String pseudocode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ComplexityDto {
        private String time;
        private String space;
    }
}
