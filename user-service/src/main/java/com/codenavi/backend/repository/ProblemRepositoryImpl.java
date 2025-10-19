package com.codenavi.backend.repository;

import com.codenavi.backend.domain.Problem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.codenavi.backend.domain.QProblem.problem;
import static com.codenavi.backend.domain.QProblemTag.problemTag;
import static com.codenavi.backend.domain.QTag.tag;

@Repository
@RequiredArgsConstructor
public class ProblemRepositoryImpl implements ProblemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Problem> findProblemsWithFilters(Pageable pageable, String category, List<String> tags, String query) {
        List<Problem> content = queryFactory
                .selectFrom(problem)
                .leftJoin(problem.problemTags, problemTag)
                .leftJoin(problemTag.tag, tag)
                .where(
                        categoryEq(category),
                        tagsIn(tags),
                        titleContains(query)
                )
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(problem.number.asc()) // 문제 번호 순으로 정렬
                .fetch();

        long total = queryFactory
                .select(problem.id.countDistinct())
                .from(problem)
                .leftJoin(problem.problemTags, problemTag)
                .leftJoin(problemTag.tag, tag)
                .where(
                        categoryEq(category),
                        tagsIn(tags),
                        titleContains(query)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // 카테고리 필터 조건
    private BooleanExpression categoryEq(String category) {
        return StringUtils.hasText(category) ? problem.metadata.category.eq(category) : null;
    }

    // 태그 필터 조건 (하나라도 포함되면)
    private BooleanExpression tagsIn(List<String> tags) {
        return tags != null && !tags.isEmpty() ? tag.name.in(tags) : null;
    }

    // 제목 검색 조건
    private BooleanExpression titleContains(String query) {
        return StringUtils.hasText(query) ? problem.title.containsIgnoreCase(query) : null;
    }
}
