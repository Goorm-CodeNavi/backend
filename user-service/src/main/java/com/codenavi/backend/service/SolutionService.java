package com.codenavi.backend.service;

import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.SolutionHistoryDto;
import com.codenavi.backend.repository.SolutionRepository;
import com.codenavi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;

    /**
     * 특정 사용자의 제출 기록 목록을 조회합니다.
     * @param username 현재 로그인한 사용자의 이름
     * @param pageable 페이지네이션 정보
     * @return 페이지 정보가 포함된 제출 기록 DTO 리스트
     */
    public Page<SolutionHistoryDto> getSolutionHistoryForUser(String username, Pageable pageable) {
        // 1. 사용자 이름으로 User 엔티티를 조회합니다.
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. 해당 사용자의 제출 기록을 페이지네이션하여 조회합니다.
        Page<Solution> solutionPage = solutionRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);

        // 3. 조회된 Page<Solution>을 Page<SolutionHistoryDto>로 변환하여 반환합니다.
        return solutionPage.map(SolutionHistoryDto::from);
    }
}
