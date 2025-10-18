package com.codenavi.backend.repository;

import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    /**
     * ID로 Solution을 조회하되, 연관된 Problem과 ThinkingProcess를 함께 즉시 로딩합니다.
     * N+1 문제를 방지하고 성능을 최적화하기 위해 Fetch Join을 사용합니다.
     * @param solutionId 조회할 풀이의 ID
     * @return Optional<Solution>
     */
    @Query("SELECT s FROM Solution s " +
            "JOIN FETCH s.problem " +
            "LEFT JOIN FETCH s.thinkingProcess " + // 사고 과정은 없을 수도 있으므로 LEFT JOIN
            "WHERE s.id = :solutionId")
    Optional<Solution> findByIdWithDetails(@Param("solutionId") Long solutionId);

    /**
     * 특정 사용자의 모든 제출 기록을 최신 제출 순으로 정렬하여 페이지네이션으로 조회합니다.
     */
    @Query("SELECT s FROM Solution s JOIN FETCH s.problem WHERE s.user = :user ORDER BY s.createdAt DESC")
    Page<Solution> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}

