package com.codenavi.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 코드 실행 API를 위한 요청 및 응답 DTO 입니다.
 */
public class CodeExecutionDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        @NotBlank(message = "언어는 필수 입력값입니다.")
        private String language;

        @NotBlank(message = "코드는 필수 입력값입니다.")
        private String code;
    }

    @Getter
    @Builder
    public static class Response {
        private int caseNumber;
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean isCorrect;
        private double executionTime; // 초 단위
        private double memoryUsed; // KB 단위
    }

    /**
     * 컴파일 에러 발생 시 응답 `result`에 포함될 DTO 입니다.
     */
    @Getter
    @Builder
    public static class CompileErrorResponse {
        private String errorType;
        private String errorMessage;
    }

    /**
     * 런타임 에러 발생 시 응답 `result`에 포함될 DTO 입니다.
     */
    @Getter
    @Builder
    public static class RuntimeErrorResponse {
        private String errorType;
        private int failedCaseNumber;
        private String input;
        private String errorMessage;
    }
}

