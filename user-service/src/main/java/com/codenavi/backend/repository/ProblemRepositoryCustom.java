package com.codenavi.backend.repository;

import com.codenavi.backend.domain.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemRepositoryCustom {
    /**
     * 동적 필터링 및 검색 조건에 따라 문제 목록을 페이지네이션하여 조회합니다.
     * @param pageable 페이지네이션 정보
     * @param category 카테고리 필터
     * @param tags 태그 필터 리스트
     * @param query 제목 검색 쿼리
     * @return 필터링된 문제 Page 객체
     */
    Page<Problem> findProblemsWithFilters(Pageable pageable, String category, List<String> tags, String query);
}
