package com.codenavi.backend.controller;

import com.codenavi.backend.dto.*;
import com.codenavi.backend.exception.CodeCompilationException;
import com.codenavi.backend.exception.CodeRuntimeException;
import com.codenavi.backend.exception.ResourceNotFoundException;
import com.codenavi.backend.service.ProblemService;
import com.codenavi.backend.service.SolutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Problem", description = "문제 관련 API")
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final SolutionService solutionService;

    @Operation(summary = "풀이 생성 및 사고 과정 최초 저장", description = "특정 문제에 대한 풀이를 시작하고 사고 과정을 최초로 저장합니다. 새로운 solutionId가 발급됩니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공적으로 생성됨", content = @Content(schema = @Schema(implementation = CreateSolutionDto.Response.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 문제 번호")
    })
    @PostMapping("/{problemNumber}/solutions")
    public ResponseEntity<ApiResponse<?>> createSolutionWithCanvas(
            @Parameter(description = "풀이를 시작할 문제의 고유 번호", required = true, example = "1000") @PathVariable String problemNumber,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "최초로 저장할 사고 과정 내용", required = true,
                    content = @Content(schema = @Schema(implementation = CreateSolutionDto.Request.class)))
            @RequestBody CreateSolutionDto.Request request,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            Long newSolutionId = solutionService.createSolutionWithCanvas(problemNumber, username, request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.onSuccess(new CreateSolutionDto.Response(newSolutionId)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    @Operation(summary = "문제 리스트 조회", description = "페이지네이션, 필터링, 검색을 지원하는 문제 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProblemList(
            @Parameter(description = "페이지 번호 (0부터 시작)") @PageableDefault(size = 10) Pageable pageable,
            @Parameter(description = "카테고리 필터 (예: 알고리즘)") @RequestParam(required = false) String category,
            @Parameter(description = "태그 필터 (영문 name, 쉼표로 구분. 예: dp,stack)") @RequestParam(required = false) List<String> tags,
            @Parameter(description = "문제 제목 검색어") @RequestParam(required = false) String query) {
        Page<ProblemListDto> problemPage = problemService.getProblemList(pageable, category, tags, query);
        return ResponseEntity.ok(ApiResponse.onSuccess(problemPage));
    }

    @Operation(summary = "추천 문제 조회", description = "로그인한 사용자를 위한 맞춤형 문제 1개를 추천합니다.", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "문제 상세 조회", description = "특정 문제의 상세 정보를 조회합니다. (문제 내용, 입출력 예시 등)")
    @GetMapping("/{problemNumber}")
    public ResponseEntity<ApiResponse<?>> getProblemDetail(@Parameter(description = "조회할 문제의 고유 번호", example = "1000") @PathVariable String problemNumber) {
        try {
            ProblemDetailDto problemDetail = problemService.getProblemDetail(problemNumber);
            return ResponseEntity.ok(ApiResponse.onSuccess(problemDetail));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    @Operation(summary = "AI 해설 조회", description = "특정 문제의 AI가 생성한 공식 해설을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{problemNumber}/editorial")
    public ResponseEntity<ApiResponse<?>> getProblemEditorial(@Parameter(description = "해설을 조회할 문제의 고유 번호", example = "1000") @PathVariable String problemNumber) {
        try {
            AiEditorialDto editorial = problemService.getProblemEditorial(problemNumber);
            return ResponseEntity.ok(ApiResponse.onSuccess(editorial));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    @Operation(summary = "코드 실행", description = "사용자 코드를 공개 테스트케이스에 대해 실행하고 결과를 즉시 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{problemNumber}/run")
    public ResponseEntity<ApiResponse<?>> runCode(
            @Parameter(description = "코드를 실행할 문제의 고유 번호", example = "1000") @PathVariable String problemNumber,
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
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.onFailure("EXEC429", "API 일일 사용량을 초과했습니다.", "토큰(사용량)이 부족합니다. 잠시 후 다시 시도해주세요."));
        }
    }
}

