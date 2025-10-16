package com.codenavi.backend.controller;

import com.codenavi.backend.dto.ApiResponse;
import com.codenavi.backend.dto.ProblemListDto;
import com.codenavi.backend.dto.RecommendedProblemDto;
import com.codenavi.backend.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    /**
     * 문제 리스트를 동적 필터링 및 페이지네이션하여 조회합니다.
     * GET /api/problems
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
}
