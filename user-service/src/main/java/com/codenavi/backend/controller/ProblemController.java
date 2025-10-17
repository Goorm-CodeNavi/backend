package com.codenavi.backend.controller;

import com.codenavi.backend.dto.*;
import com.codenavi.backend.exception.CodeCompilationException;
import com.codenavi.backend.exception.ResourceNotFoundException;
import com.codenavi.backend.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/{problemNumber}")
    public ResponseEntity<ApiResponse<?>> getProblemDetail(@PathVariable String problemNumber) {
        try {
            ProblemDetailDto problemDetail = problemService.getProblemDetail(problemNumber);
            return ResponseEntity.ok(ApiResponse.onSuccess(problemDetail));
        } catch (ResourceNotFoundException e) {
            // Service에서 예외가 발생하면 404 응답을 반환합니다.
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProblemList(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String query) {

        Page<ProblemListDto> problemPage = problemService.getProblemList(pageable, category, tags, query);
        return ResponseEntity.ok(ApiResponse.onSuccess(problemPage));
    }
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<?>> getRecommendedProblem(Authentication authentication) {
        // JWT 토큰에서 사용자 이름(username)을 가져옵니다.
        String username = authentication.getName();

        try {
            RecommendedProblemDto recommendedProblem = problemService.recommendProblemForUser(username);
            return ResponseEntity.ok(ApiResponse.onSuccess(recommendedProblem));
        } catch (RuntimeException e) {
            // 서비스에서 "추천할 문제가 없습니다" 예외가 발생한 경우
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    @GetMapping("/{problemNumber}/editorial")
    public ResponseEntity<ApiResponse<?>> getProblemEditorial(@PathVariable String problemNumber) {
        try {
            AiEditorialDto editorial = problemService.getProblemEditorial(problemNumber);
            return ResponseEntity.ok(ApiResponse.onSuccess(editorial));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    @PostMapping("/{problemNumber}/run")
    public ResponseEntity<ApiResponse<?>> runCode(
            @PathVariable String problemNumber,
            @Valid @RequestBody CodeExecutionDto.Request request,
            Authentication authentication) {
        try {
            List<CodeExecutionDto.Response> results = problemService.runCode(problemNumber, request);
            return ResponseEntity.ok(ApiResponse.onSuccess(results));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));

        } catch (CodeCompilationException e) {
            // 컴파일 에러 DTO 생성
            CodeExecutionDto.CompileErrorResponse errorResponse = CodeExecutionDto.CompileErrorResponse.builder()
                    .errorType("Compile Error")
                    .errorMessage(e.getCompileErrorMessage())
                    .build();

            // 422 Unprocessable Entity 응답 반환
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ApiResponse.onFailure("EXEC4221", "코드를 처리할 수 없습니다.", errorResponse));
        }
    }
}
