package com.codenavi.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"}) // JSON 응답 순서 지정
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // result 필드가 null일 경우 JSON에 포함하지 않음
    private T result;

    // 성공 시 응답 (결과 데이터 포함)
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, "COMMON200", "성공입니다.", result);
    }

    // 성공 시 응답 (결과 데이터만 포함, 코드/메시지 기본값 사용)
    public static <T> ApiResponse<T> onSuccess(T result, String message) {
        return new ApiResponse<>(true, "COMMON200", message, result);
    }

    // 실패 시 응답
    public static <T> ApiResponse<T> onFailure(String code, String message, T result) {
        return new ApiResponse<>(false, code, message, result);
    }
}