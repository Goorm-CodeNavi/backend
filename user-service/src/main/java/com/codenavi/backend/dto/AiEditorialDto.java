package com.codenavi.backend.dto;

import com.codenavi.backend.domain.Problem;
import lombok.Builder;
import lombok.Getter;

/**
 * AI 해설 조회 API의 응답 DTO 입니다.
 */
@Getter
@Builder
public class AiEditorialDto {

    private String summary;
    private String strategy;
    private ComplexityDto complexity;
    private String pseudocode;

    @Getter
    @Builder
    public static class ComplexityDto {
        private String timeAndSpace;
    }

    /**
     * Problem 엔티티에서 Editorial 정보를 추출하여 DTO로 변환합니다.
     * @param problem 조회된 Problem 엔티티
     * @return 변환된 AiEditorialDto 객체
     */
    public static AiEditorialDto from(Problem problem) {
        // Problem 엔티티 내부에 @Embedded로 정의된 Editorial 객체를 가져옵니다.
        Problem.Editorial editorial = problem.getEditorial();
        Problem.Complexity complexity = editorial.getComplexity();

        return AiEditorialDto.builder()
                .summary(editorial.getSummary())
                .strategy(editorial.getStrategy())
                .complexity(ComplexityDto.builder()
                        .timeAndSpace(complexity.getTimeAndSpace())
                        .build())
                .pseudocode(editorial.getPseudocode())
                .build();
    }
}

