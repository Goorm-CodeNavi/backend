package com.codenavi.backend.repository;

import com.codenavi.backend.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {

    Optional<Problem> findByNumber(String number);

    @Query("SELECT p FROM Problem p WHERE p.id NOT IN (SELECT s.problem.id FROM Solution s WHERE s.user.id = :userId)")
    List<Problem> findUnsolvedProblemsByUserId(@Param("userId") Long userId);


}
