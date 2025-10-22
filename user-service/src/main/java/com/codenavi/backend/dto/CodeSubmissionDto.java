package com.codenavi.backend.dto;

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

        @NotNull(message = "풀이 시간은 필수 입력값입니다.")
        private Long timeSpent; // 프론트에서 받은 측정 시간 (ms 단위)
    }

    @Getter
    @Builder
    public static class Response {
        private Long solutionId;
        private String status;
        private Integer failedCaseNumber; // 오답일 경우에만 사용
        private Double executionTime;     // 정답일 경우에만 사용 (평균 또는 최대 시간)
        private Double memoryUsed;        // 정답일 경우에만 사용 (평균 또는 최대 메모리)
    }
}

