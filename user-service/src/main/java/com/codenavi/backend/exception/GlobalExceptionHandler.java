package com.codenavi.backend.exception;

import com.codenavi.backend.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

// 모든 @RestController에서 발생하는 예외를 처리하는 클래스
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 처리하지 못한 모든 예외는 여기서 처리됩니다. (최후의 보루)
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex, WebRequest request) {
        // 1. 어떤 에러가 발생했는지 서버 로그에 반드시 기록합니다.
        // ex.getMessage() : 에러 메시지
        // request.getDescription(false) : 요청 URI
        log.error("### GlobalException: {} [Request: {}]", ex.getMessage(), request.getDescription(false));

        // 2. 클라이언트에게는 일관된 형식의 500 에러 응답을 보냅니다.
        //    (상세한 에러 내용은 보안상 노출하지 않습니다.)
        ApiResponse<?> errorResponse = ApiResponse.onFailure(
                "COMMON500",
                "서버에 오류가 발생했습니다.",
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 필요하다면 특정 예외를 위한 핸들러를 추가할 수 있습니다.
    // 예: @ExceptionHandler(CustomException.class)
}
