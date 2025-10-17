package com.codenavi.backend.service;

import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.domain.ThinkingProcess;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.CreateSolutionDto;
import com.codenavi.backend.dto.SolutionHistoryDto;
import com.codenavi.backend.dto.ThinkingCanvasDto;
import com.codenavi.backend.exception.ResourceNotFoundException;
import com.codenavi.backend.repository.ProblemRepository;
import com.codenavi.backend.repository.SolutionRepository;
import com.codenavi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime; // LocalDateTime import 추가

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    /**
     * 특정 사용자의 제출 기록 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<SolutionHistoryDto> getSolutionHistoryForUser(String username, Pageable pageable) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        Page<Solution> solutionPage = solutionRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        return solutionPage.map(SolutionHistoryDto::from);
    }

    /**
     * 새로운 풀이와 사고 과정을 생성하고, 생성된 solutionId를 반환합니다.
     */
    @Transactional
    public Long createSolutionWithCanvas(String problemNumber, String username, CreateSolutionDto.Request request) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        Problem problem = problemRepository.findByNumber(problemNumber)
                .orElseThrow(() -> new ResourceNotFoundException("해당 번호의 문제를 찾을 수 없습니다."));

        Solution newSolution = new Solution();
        newSolution.setUser(currentUser);
        newSolution.setProblem(problem);

        ThinkingProcess thinkingProcess = new ThinkingProcess();
        // Call the new core helper method
        updateThinkingProcessCore(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(), request.getComplexityAnalysis(), request.getPseudocode());
        thinkingProcess.setSolution(newSolution);
        newSolution.setThinkingProcess(thinkingProcess);

        newSolution.setCreatedAt(LocalDateTime.now());

        Solution savedSolution = solutionRepository.save(newSolution);
        return savedSolution.getId();
    }

    /**
     * 기존 풀이의 사고 과정 캔버스 내용을 업데이트합니다.
     */
    @Transactional
    public void updateThinkingCanvas(Long solutionId, String username, ThinkingCanvasDto.Request request) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 풀이 기록을 찾을 수 없습니다."));

        if (!solution.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("자신의 풀이 기록에만 접근할 수 있습니다.");
        }

        ThinkingProcess thinkingProcess = solution.getThinkingProcess();
        if (thinkingProcess == null) {
            thinkingProcess = new ThinkingProcess();
            thinkingProcess.setSolution(solution);
            solution.setThinkingProcess(thinkingProcess);
        }

        // Call the new core helper method
        updateThinkingProcessCore(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(), request.getComplexityAnalysis(), request.getPseudocode());
    }

    /**
     * [Core Helper Method] DTO의 내용으로 ThinkingProcess 엔티티를 업데이트하는 중복 로직
     */
    private void updateThinkingProcessCore(
            ThinkingProcess thinkingProcess,
            String summaryContent,
            String strategyContent,
            ThinkingCanvasDto.ComplexityDto complexityDto,
            String pseudocodeContent
    ) {
        ThinkingProcess.ProblemSummary summary = new ThinkingProcess.ProblemSummary();
        summary.setContent(summaryContent);
        thinkingProcess.setProblemSummary(summary);

        ThinkingProcess.SolutionStrategy strategy = new ThinkingProcess.SolutionStrategy();
        strategy.setContent(strategyContent);
        thinkingProcess.setSolutionStrategy(strategy);

        ThinkingProcess.ComplexityAnalysis complexity = new ThinkingProcess.ComplexityAnalysis();
        if (complexityDto != null) {
            complexity.setTimeComplexity(complexityDto.getTime());
            complexity.setSpaceComplexity(complexityDto.getSpace());
        }
        thinkingProcess.setComplexityAnalysis(complexity);

        ThinkingProcess.Pseudocode pseudocode = new ThinkingProcess.Pseudocode();
        pseudocode.setContent(pseudocodeContent);
        thinkingProcess.setPseudocode(pseudocode);
    }
}