package com.codenavi.backend.dto;

import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.service.SolutionService;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 제출 기록 목록 조회 API의 응답 DTO 입니다.
 */
@Getter
@Builder
public class SolutionHistoryDto {

    private Long solutionId;
    private String problemNumber;
    private String problemTitle;
    private String status;
    private String language;
    private LocalDateTime submittedAt;

    /**
     * Solution 엔티티를 API 응답에 필요한 DTO 형태로 변환합니다.
     * @param solution 조회된 Solution 엔티티 객체
     * @return 변환된 DTO 객체
     */
    public static SolutionHistoryDto from(Solution solution) {
        return SolutionHistoryDto.builder()
                .solutionId(solution.getId())
                .problemNumber(solution.getProblem().getNumber())
                .problemTitle(solution.getProblem().getTitle())
                // 'status' 필드는 isCorrect 값에 따라 "정답" 또는 "오답"으로 설정
                .status(solution.getStatus().isCorrect() ? "정답" : "오답")
                .language(solution.getImplementation().getLanguage())
                .submittedAt(solution.getCreatedAt()) // Solution 엔티티에 생성일자 필드(createdAt)가 있다고 가정
                .build();
    }


}
