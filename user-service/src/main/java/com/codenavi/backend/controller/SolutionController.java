package com.codenavi.backend.controller;

import com.codenavi.backend.dto.ApiResponse;
import com.codenavi.backend.dto.CreateSolutionDto;
import com.codenavi.backend.dto.ThinkingCanvasDto;
import com.codenavi.backend.exception.ResourceNotFoundException;
import com.codenavi.backend.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    /**
     * [신규] 풀이 생성 및 사고 과정 최초 저장
     */
    @PostMapping("/problems/{solutionId}/solutions")
    public ResponseEntity<ApiResponse<?>> createSolutionWithCanvas(
            @PathVariable String solutionId,
            @RequestBody CreateSolutionDto.Request request,
            Authentication authentication) {

        String username = authentication.getName();
        Long newSolutionId = solutionService.createSolutionWithCanvas(solutionId, username, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(new CreateSolutionDto.Response(newSolutionId)));
    }

    /**
     * [기존] 사고 과정 수정 (업데이트)
     */
    @PostMapping("/solutions/{solutionId}/canvas")
    public ResponseEntity<ApiResponse<?>> updateThinkingCanvas(
            @PathVariable Long solutionId,
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
}

