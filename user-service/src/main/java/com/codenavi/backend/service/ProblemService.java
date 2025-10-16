package com.codenavi.backend.service;

import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.ProblemListDto;
import com.codenavi.backend.dto.RecommendedProblemDto;
import com.codenavi.backend.repository.ProblemRepository;
import com.codenavi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    /**
     * 필터링 조건에 맞는 문제 목록을 조회하고, DTO로 변환하여 반환합니다.
     */
    public Page<ProblemListDto> getProblemList(Pageable pageable, String category, List<String> tags, String query) {
        Page<Problem> problems = problemRepository.findProblemsWithFilters(pageable, category, tags, query);
        return problems.map(ProblemListDto::from);
    }

    public RecommendedProblemDto recommendProblemForUser(String username) {
        // 1. 현재 사용자 정보 조회
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. 사용자가 아직 풀지 않은 문제 목록 조회
        List<Problem> unsolvedProblems = problemRepository.findUnsolvedProblemsByUserId(currentUser.getId());

        // 3. 추천할 문제가 없는 경우 예외 처리
        if (unsolvedProblems.isEmpty()) {
            // 이 예외는 GlobalExceptionHandler에서 404 Not Found로 처리될 수 있습니다.
            throw new RuntimeException("추천할 문제가 없습니다.");
        }

        // 4. 간단한 추천 로직 (랜덤 선택)
        Problem recommendedProblem = unsolvedProblems.get(new Random().nextInt(unsolvedProblems.size()));

        // 5. DTO로 변환하여 반환
        return RecommendedProblemDto.from(recommendedProblem);
    }
}
