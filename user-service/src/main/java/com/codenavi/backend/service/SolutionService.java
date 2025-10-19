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
import com.codenavi.backend.client.Judge0Client;
import com.codenavi.backend.domain.*;
import com.codenavi.backend.dto.*;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import java.time.LocalDateTime; // LocalDateTime import 추가

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final ProblemRepository problemRepository;
    private final Judge0Client judge0Client;

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

        Solution.Implementation implementation = new Solution.Implementation();
        implementation.setLanguage("");
        implementation.setCode("");
        implementation.setImplementationTime(0L);
        newSolution.setImplementation(implementation);

        ThinkingProcess thinkingProcess = new ThinkingProcess();
        updateThinkingProcessFromDto(thinkingProcess, request);
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
        updateThinkingProcessFromDto(thinkingProcess, request);
    }

    @Transactional(readOnly = true)
    public Page<SolutionHistoryDto> getSolutionHistoryForUser(String username, Pageable pageable) {
        // 1. 사용자 이름으로 User 엔티티를 조회합니다.
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. 해당 사용자의 제출 기록을 페이지네이션하여 조회합니다.
        Page<Solution> solutionPage = solutionRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);

        // 3. 조회된 Page<Solution>을 Page<SolutionHistoryDto>로 변환하여 반환합니다.
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

    @Transactional
    public CodeSubmissionDto.Response submitCode(Long solutionId, String username, CodeSubmissionDto.Request request) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // --- 👇 수정된 부분 ---
        // 기본 findById 대신, 연관된 엔티티(Problem 등)를 함께 불러오는 findByIdWithDetails를 사용합니다.
        Solution solution = solutionRepository.findByIdWithDetails(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 풀이 기록을 찾을 수 없습니다."));
        // -------------------

        if (!solution.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("자신의 풀이에만 코드를 제출할 수 있습니다.");
        }

        // 제출된 코드, 언어, 측정 시간, 최종 사고 과정을 Solution 엔티티에 업데이트합니다.
        solution.getImplementation().setCode(request.getCode());
        solution.getImplementation().setLanguage(request.getLanguage());
        solution.getImplementation().setImplementationTime(request.getTimeSpent());
        updateThinkingProcessFromDto(solution.getThinkingProcess(), request);

        List<TestCase> allTestCases = solution.getProblem().getTestCases();
        int languageId = mapLanguageToJudge0Id(request.getLanguage());

        double totalTime = 0;
        double maxMemory = 0;

        for (int i = 0; i < allTestCases.size(); i++) {
            TestCase tc = allTestCases.get(i);
            Judge0Client.Judge0Result result = judge0Client.execute(languageId, request.getCode(), tc.getInput());

            String actualOutput = result.getStdout() != null ? result.getStdout().trim() : "";
            boolean isCorrect = actualOutput.equals(tc.getExpectedOutput().trim());

            totalTime += (result.getTime() != null ? result.getTime() : 0);
            if (result.getMemory() != null && result.getMemory() > maxMemory) {
                maxMemory = result.getMemory();
            }

            if (!isCorrect) {
                solution.getStatus().setCurrentStatus("Wrong Answer");
                solution.getStatus().setCorrect(false);
                solutionRepository.save(solution);
                return CodeSubmissionDto.Response.builder()
                        .solutionId(solutionId)
                        .status("Wrong Answer")
                        .failedCaseNumber(i + 1)
                        .build();
            }
        }

        solution.getStatus().setCurrentStatus("Accepted");
        solution.getStatus().setCorrect(true);
        solutionRepository.save(solution);

        return CodeSubmissionDto.Response.builder()
                .solutionId(solutionId)
                .status("Accepted")
                .executionTime(allTestCases.isEmpty() ? 0 : totalTime / allTestCases.size())
                .memoryUsed(maxMemory)
                .build();
    }

    // [Helper] for CreateSolutionDto
    private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, CreateSolutionDto.Request request) {
        updateThinkingProcessLogic(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(), request.getComplexityAnalysis(), request.getPseudocode());
    }

    // [Helper] for ThinkingCanvasDto
    private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, ThinkingCanvasDto.Request request) {
        updateThinkingProcessLogic(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(), request.getComplexityAnalysis(), request.getPseudocode());
    }

    // [Helper] for CodeSubmissionDto
    private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, CodeSubmissionDto.Request request) {
        updateThinkingProcessLogic(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(), request.getComplexityAnalysis(), request.getPseudocode());
    }

    // [Central Logic]
    private void updateThinkingProcessLogic(ThinkingProcess thinkingProcess, String summaryContent, String strategyContent, ThinkingCanvasDto.ComplexityDto complexityDto, String pseudocodeContent) {
        if (thinkingProcess == null) return;

        if (thinkingProcess.getProblemSummary() == null) thinkingProcess.setProblemSummary(new ThinkingProcess.ProblemSummary());
        thinkingProcess.getProblemSummary().setContent(summaryContent);

        if (thinkingProcess.getSolutionStrategy() == null) thinkingProcess.setSolutionStrategy(new ThinkingProcess.SolutionStrategy());
        thinkingProcess.getSolutionStrategy().setContent(strategyContent);

        if (thinkingProcess.getComplexityAnalysis() == null) thinkingProcess.setComplexityAnalysis(new ThinkingProcess.ComplexityAnalysis());
        if (complexityDto != null) {
            thinkingProcess.getComplexityAnalysis().setTimeComplexity(complexityDto.getTimeAndSpace());
        }

        if (thinkingProcess.getPseudocode() == null) thinkingProcess.setPseudocode(new ThinkingProcess.Pseudocode());
        thinkingProcess.getPseudocode().setContent(pseudocodeContent);
    }

    private int mapLanguageToJudge0Id(String language) {
        switch (language.toLowerCase()) {
            case "java": return 62;
            case "python": return 71;
            case "javascript": return 63;
            case "c": return 48;
            default: throw new IllegalArgumentException("지원하지 않는 언어입니다: " + language);
        }
    }
    @Transactional(readOnly = true)
    public SolutionDetailDto getSolutionDetail(Long solutionId, String username) {
        // 1. 현재 사용자 정보를 조회합니다.
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. solutionId로 풀이 기록을 상세 정보와 함께 조회합니다. 없으면 404 예외를 던집니다.
        Solution solution = solutionRepository.findByIdWithDetails(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 제출 기록을 찾을 수 없습니다."));

        // 3. 해당 풀이가 현재 로그인한 사용자의 것인지 권한을 확인합니다.
        if (!solution.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("자신의 제출 기록만 조회할 수 있습니다.");
        }

        // 4. 조회된 엔티티를 DTO로 변환하여 반환합니다.
        return SolutionDetailDto.from(solution);
    }
}