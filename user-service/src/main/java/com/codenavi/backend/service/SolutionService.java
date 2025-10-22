package com.codenavi.backend.service;

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

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
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
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        Page<Solution> solutionPage = solutionRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        return solutionPage.map(SolutionHistoryDto::from);
    }

    @Transactional(readOnly = true)
    public SolutionDetailDto getSolutionDetail(Long solutionId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        Solution solution = solutionRepository.findByIdWithDetails(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 제출 기록을 찾을 수 없습니다."));

        if (!solution.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("자신의 제출 기록만 조회할 수 있습니다.");
        }
        return SolutionDetailDto.from(solution);
    }

    @Transactional
    public CodeSubmissionDto.Response submitCode(Long solutionId, String username, CodeSubmissionDto.Request request) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        Solution solution = solutionRepository.findByIdWithDetails(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 풀이 기록을 찾을 수 없습니다."));

        if (!solution.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("자신의 풀이에만 코드를 제출할 수 있습니다.");
        }

        // --- 👇 수정된 부분 ---
        // 제출된 코드, 언어, 측정 시간만 업데이트합니다. (사고 과정은 업데이트하지 않음)
        solution.getImplementation().setCode(request.getCode());
        solution.getImplementation().setLanguage(request.getLanguage());
        solution.getImplementation().setImplementationTime(request.getTimeSpent());
        // 'updateThinkingProcessFromDto(solution.getThinkingProcess(), request);' 라인을 제거했습니다.
        // -------------------

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

    private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, CreateSolutionDto.Request request) {
        updateThinkingProcessLogic(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(), request.getComplexityAnalysis(), request.getPseudocode());
    }

    private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, ThinkingCanvasDto.Request request) {
        updateThinkingProcessLogic(thinkingProcess, request.getProblemSummary(), request.getSolutionStrategy(), request.getComplexityAnalysis(), request.getPseudocode());
    }

    // --- 👇 수정된 부분: CodeSubmissionDto.Request를 받는 헬퍼 메소드를 제거했습니다. ---
    // private void updateThinkingProcessFromDto(ThinkingProcess thinkingProcess, CodeSubmissionDto.Request request) { ... }
    // --------------------------------------------------------------------

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
            default: throw new IllegalArgumentException("지원하지 않는 언어입니다: " + language);
        }
    }
}

