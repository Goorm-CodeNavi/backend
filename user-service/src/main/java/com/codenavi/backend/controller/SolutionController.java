package com.codenavi.backend.controller;

import com.codenavi.backend.dto.ApiResponse;
import com.codenavi.backend.dto.CodeExecutionDto;
import com.codenavi.backend.dto.CodeSubmissionDto;
import com.codenavi.backend.dto.SolutionDetailDto;
import com.codenavi.backend.dto.ThinkingCanvasDto;
import com.codenavi.backend.exception.CodeCompilationException;
import com.codenavi.backend.exception.CodeRuntimeException;
import com.codenavi.backend.exception.ResourceNotFoundException;
import com.codenavi.backend.service.SolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

/**
 * '풀이(Solution)'와 관련된 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    @PostMapping("/{solutionId}/canvas")
    public ResponseEntity<ApiResponse<?>> updateThinkingCanvas(
            @PathVariable Long solutionId,
            @RequestBody ThinkingCanvasDto.Request request,
            Authentication authentication) {
        String username = authentication.getName();
        try {
            solutionService.updateThinkingCanvas(solutionId, username, request);
            return ResponseEntity.ok(ApiResponse.onSuccess("사고 과정이 성공적으로 업데이트되었습니다."));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.onFailure("AUTH4003", "권한이 없습니다.", e.getMessage()));
        }
    }

    @GetMapping("/{solutionId}")
    public ResponseEntity<ApiResponse<?>> getSolutionDetail(
            @PathVariable Long solutionId,
            Authentication authentication) {

        String username = authentication.getName();

        try {
            SolutionDetailDto solutionDetail = solutionService.getSolutionDetail(solutionId, username);
            return ResponseEntity.ok(ApiResponse.onSuccess(solutionDetail));

        } catch (ResourceNotFoundException e) {
            // Service에서 ResourceNotFoundException이 발생하면 404 응답을 반환합니다.
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));

        } catch (AccessDeniedException e) {
            // Service에서 AccessDeniedException이 발생하면 403 응답을 반환합니다.
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure("AUTH4003", "권한이 없습니다.", e.getMessage()));
        }
    }

    @PostMapping("/{solutionId}/submit")
    public ResponseEntity<ApiResponse<?>> submitCode(
            @PathVariable Long solutionId,
            @Valid @RequestBody CodeSubmissionDto.Request request,
            Authentication authentication) {

        String username = authentication.getName();

        try {
            CodeSubmissionDto.Response result = solutionService.submitCode(solutionId, username, request);
            String message = "Accepted".equals(result.getStatus()) ? "정답입니다!" : "오답입니다.";
            return ResponseEntity.ok(ApiResponse.onSuccess(result, message));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure("AUTH003", "권한이 없습니다.", e.getMessage()));
        } catch (CodeCompilationException e) {
            CodeExecutionDto.CompileErrorResponse errorResponse = CodeExecutionDto.CompileErrorResponse.builder()
                    .errorType("Compile Error")
                    .errorMessage(e.getCompileErrorMessage())
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ApiResponse.onFailure("EXEC4221", e.getMessage(), errorResponse));
        } catch (CodeRuntimeException e) {
            CodeExecutionDto.RuntimeErrorResponse errorResponse = CodeExecutionDto.RuntimeErrorResponse.builder()
                    .errorType("Runtime Error")
                    .failedCaseNumber(e.getFailedCaseNumber())
                    .input(e.getInput())
                    .errorMessage(e.getRuntimeErrorMessage())
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ApiResponse.onFailure("EXEC4222", e.getMessage(), errorResponse));
        } catch (HttpClientErrorException.TooManyRequests e) {
            // --- 👇 수정된 부분: 429 에러를 별도로 처리합니다. ---
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS) // 429 상태 코드 반환
                    .body(ApiResponse.onFailure("EXEC429", "API 일일 사용량을 초과했습니다.", "토큰(사용량)이 부족합니다. 잠시 후 다시 시도해주세요."));
            // ------------------------------------------------
        }
    }
}

