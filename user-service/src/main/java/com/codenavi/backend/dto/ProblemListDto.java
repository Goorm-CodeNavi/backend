package com.codenavi.backend.dto;

import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.ProblemTag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProblemListDto {

    private String number;
    private String title;
    private String category;
    private List<String> tags;

    /**
     * Problem 엔티티를 API 응답에 필요한 DTO 형태로 변환합니다.
     * @param problem 조회된 Problem 엔티티 객체
     * @return 변환된 DTO 객체
     */
    public static ProblemListDto from(Problem problem) {
        // ProblemTag 목록에서 태그의 표시 이름(displayName)만 추출하여 리스트로 만듭니다.
        List<String> tagNames = problem.getProblemTags().stream()
                .map(problemTag -> problemTag.getTag().getDisplayName())
                .collect(Collectors.toList());

        return ProblemListDto.builder()
                .number(problem.getNumber())
                .title(problem.getTitle())
                .category(problem.getMetadata().getCategory())
                .tags(tagNames)
                .build();
    }
}
