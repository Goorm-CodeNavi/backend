package com.codenavi.backend.repository;

import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    /**
     * 특정 사용자의 모든 제출 기록을 최신 제출 순으로 정렬하여 페이지네이션으로 조회합니다.
     * Spring Data JPA의 쿼리 메소드 기능을 사용하여 자동으로 쿼리가 생성됩니다.
     * @param user 조회할 사용자 엔티티
     * @param pageable 페이지네이션 정보 (페이지 번호, 사이즈)
     * @return 페이지네이션된 Solution 리스트
     */
    Page<Solution> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}