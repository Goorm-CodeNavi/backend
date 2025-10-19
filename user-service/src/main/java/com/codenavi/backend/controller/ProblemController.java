package com.codenavi.backend.controller;

import com.codenavi.backend.dto.*;
import com.codenavi.backend.exception.CodeCompilationException;
import com.codenavi.backend.exception.CodeRuntimeException;
import com.codenavi.backend.exception.ResourceNotFoundException;
import com.codenavi.backend.service.ProblemService;
import com.codenavi.backend.service.SolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

/**
 * '문제(Problem)'와 관련된 API 요청을 처리하는 컨트롤러입니다.
 * (문제 리스트/상세/추천/해설 조회, 코드 실행, 풀이 생성)
 */
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final SolutionService solutionService; // 풀이 생성을 위해 SolutionService 주입

    /**
     * [신규] 특정 문제에 대한 풀이를 생성하고 사고 과정을 최초로 저장합니다.
     */
    @PostMapping("/{problemNumber}/solutions")
    public ResponseEntity<ApiResponse<?>> createSolutionWithCanvas(
            @PathVariable String problemNumber,
            @RequestBody CreateSolutionDto.Request request,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            Long newSolutionId = solutionService.createSolutionWithCanvas(problemNumber, username, request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.onSuccess(new CreateSolutionDto.Response(newSolutionId)));
        } catch (ResourceNotFoundException e) {
            // Service에서 "문제를 찾을 수 없음" 예외가 발생하면 404 응답을 반환합니다.
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }
    /**
     * 문제 리스트 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProblemList(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String query) {
        Page<ProblemListDto> problemPage = problemService.getProblemList(pageable, category, tags, query);
        return ResponseEntity.ok(ApiResponse.onSuccess(problemPage));
    }

    /**
     * 추천 문제 조회 API
     */
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<?>> getRecommendedProblem(Authentication authentication) {
        String username = authentication.getName();
        try {
            RecommendedProblemDto recommendedProblem = problemService.recommendProblemForUser(username);
            return ResponseEntity.ok(ApiResponse.onSuccess(recommendedProblem));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    /**
     * 문제 상세 조회 API
     */
    @GetMapping("/{problemNumber}")
    public ResponseEntity<ApiResponse<?>> getProblemDetail(@PathVariable String problemNumber) {
        try {
            ProblemDetailDto problemDetail = problemService.getProblemDetail(problemNumber);
            return ResponseEntity.ok(ApiResponse.onSuccess(problemDetail));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    /**
     * AI 해설 조회 API
     */
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

    /**
     * 코드 실행 API
     */
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

