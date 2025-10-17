package com.codenavi.backend.service;

import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.Solution;
import com.codenavi.backend.domain.ThinkingProcess;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.CreateSolutionDto;
import com.codenavi.backend.dto.SolutionDetailDto;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @Transactional
    public Long createSolutionWithCanvas(String problemNumber, String username, CreateSolutionDto.Request request) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        Problem problem = problemRepository.findByNumber(problemNumber)
                .orElseThrow(() -> new ResourceNotFoundException("해당 번호의 문제를 찾을 수 없습니다."));

        Solution newSolution = new Solution();
        newSolution.setUser(currentUser);
        newSolution.setProblem(problem);

        Solution.Status status = new Solution.Status();
        status.setCurrentStatus("풀이 중");
        status.setCorrect(false);
        newSolution.setStatus(status);

        // --- 👇 수정된 부분 ---
        // Implementation 객체를 생성하고 필드를 기본값으로 초기화합니다.
        Solution.Implementation implementation = new Solution.Implementation();
        implementation.setLanguage(""); // null 대신 빈 문자열로 초기화
        implementation.setCode("");     // null 대신 빈 문자열로 초기화
        implementation.setImplementationTime(0L); // 0으로 초기화
        newSolution.setImplementation(implementation);
        // -------------------

        ThinkingProcess thinkingProcess = new ThinkingProcess();
        updateThinkingProcessFromDto(thinkingProcess, request); // 생성용 헬퍼 호출
        thinkingProcess.setSolution(newSolution);
        newSolution.setThinkingProcess(thinkingProcess);

        newSolution.setCreatedAt(LocalDateTime.now());

        Solution savedSolution = solutionRepository.save(newSolution);
        return savedSolution.getId();
    }

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

        updateThinkingProcessFromDto(thinkingProcess, request); // 수정용 헬퍼 호출
    }

    @Transactional(readOnly = true)
    public Page<SolutionHistoryDto> getSolutionHistoryForUser(String username, Pageable pageable) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        Page<Solution> solutionPage = solutionRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);

        return solutionPage.map(SolutionHistoryDto::from);
    }

    @Transactional(readOnly = true)
    public SolutionDetailDto getSolutionDetail(Long solutionId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 제출 기록을 찾을 수 없습니다."));

        if (!solution.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("자신의 제출 기록만 조회할 수 있습니다.");
        }

        return SolutionDetailDto.from(solution);
    }

    /**
     * [Helper Method for Create] CreateSolutionDto.Request로 ThinkingProcess를 업데이트합니다.
     */
    private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, CreateSolutionDto.Request request) {
        // 중앙 로직 호출
        updateThinkingProcessLogic(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(),
                request.getComplexityAnalysis(), request.getPseudocode());
    }

    /**
     * [Helper Method for Update] ThinkingCanvasDto.Request로 ThinkingProcess를 업데이트합니다. (Overloading)
     */
    private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, ThinkingCanvasDto.Request request) {
        // 중앙 로직 호출
        updateThinkingProcessLogic(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(),
                request.getComplexityAnalysis(), request.getPseudocode());
    }

    /**
     * [Central Logic] 두 DTO의 공통 로직을 처리하는 중앙 메소드입니다.
     */
    private void updateThinkingProcessLogic(ThinkingProcess thinkingProcess, String summaryContent,
                                            String strategyContent, ThinkingCanvasDto.ComplexityDto complexityDto,
                                            String pseudocodeContent) {

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

