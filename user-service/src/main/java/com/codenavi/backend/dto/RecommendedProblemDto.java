package com.codenavi.backend.dto;

import com.codenavi.backend.domain.Problem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedProblemDto {

    private String title;
    private String content;
    private String inputDescription;
    private String outputDescription;
    private String link;

    public static RecommendedProblemDto from(Problem problem) {
        // 문제 상세 페이지로 이동할 링크를 생성합니다. (예: /problems/1002)
        String problemLink = "/problems/" + problem.getNumber();

        return RecommendedProblemDto.builder()
                .title(problem.getTitle())
                .content(problem.getDescription().getContent())
                .inputDescription(problem.getDescription().getInputDescription())
                .outputDescription(problem.getDescription().getOutputDescription())
                .link(problemLink)
                .build();
    }
}

