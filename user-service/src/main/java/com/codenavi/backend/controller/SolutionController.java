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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * (사고 과정 저장/수정, 상세 기록 조회, 코드 최종 제출)
 */
@Tag(name = "Solution", description = "풀이 관련 API")
@RestController
@RequestMapping("/api/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    @Operation(summary = "사고 과정 캔버스 수정", description = "기존에 생성된 풀이(Solution)의 사고 과정 내용을 업데이트합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공적으로 업데이트됨"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 풀이)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 풀이 ID")
    })
    @PostMapping("/{solutionId}/canvas")
    public ResponseEntity<ApiResponse<?>> updateThinkingCanvas(
            @Parameter(description = "수정할 풀이의 ID") @PathVariable Long solutionId,
            @RequestBody ThinkingCanvasDto.Request request,
            Authentication authentication) {

        String username = authentication.getName();

        try {
            solutionService.updateThinkingCanvas(solutionId, username, request);
            return ResponseEntity.ok(ApiResponse.onSuccess("사고 과정이 성공적으로 업데이트되었습니다."));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure("AUTH4003", "권한이 없습니다.", e.getMessage()));
        }
    }

    @Operation(summary = "상세 제출 기록 조회", description = "특정 풀이(Solution)의 문제 정보, 사고 과정, 제출 코드 등 모든 상세 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SolutionDetailDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 풀이 ID")
    })
    @GetMapping("/{solutionId}")
    public ResponseEntity<ApiResponse<?>> getSolutionDetail(
            @Parameter(description = "조회할 풀이의 ID") @PathVariable Long solutionId,
            Authentication authentication) {

        String username = authentication.getName();

        try {
            SolutionDetailDto solutionDetail = solutionService.getSolutionDetail(solutionId, username);
            return ResponseEntity.ok(ApiResponse.onSuccess(solutionDetail));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("COMMON404", "데이터를 찾을 수 없습니다.", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure("AUTH4003", "권한이 없습니다.", e.getMessage()));
        }
    }

    @Operation(summary = "코드 최종 제출", description = "사용자가 작성한 코드와 최종 사고 과정을 채점하고 결과를 DB에 저장합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채점 성공 (정답/오답)", content = @Content(schema = @Schema(implementation = CodeSubmissionDto.Response.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (요청 본문 유효성 검사 실패)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 풀이에 접근)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음 (존재하지 않는 solutionId)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "처리할 수 없는 코드 (컴파일 또는 런타임 에러)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "API 사용량 초과 (채점 API 일일 할당량 소진)")
    })
    @PostMapping("/{solutionId}/submit")
    public ResponseEntity<ApiResponse<?>> submitCode(
            @Parameter(description = "코드를 제출할 풀이의 ID", required = true, example = "101") @PathVariable Long solutionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "제출할 코드, 언어, 최종 사고 과정", required = true,
                    content = @Content(schema = @Schema(implementation = CodeSubmissionDto.Request.class)))
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
                    .body(ApiResponse.onFailure("AUTH4003", "권한이 없습니다.", e.getMessage()));
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

