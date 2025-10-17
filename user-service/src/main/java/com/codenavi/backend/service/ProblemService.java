package com.codenavi.backend.service;

import com.codenavi.backend.client.Judge0Client;
import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.TestCase;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.AiEditorialDto;
import com.codenavi.backend.dto.ProblemDetailDto;
import com.codenavi.backend.dto.ProblemListDto;
import com.codenavi.backend.dto.RecommendedProblemDto;
import com.codenavi.backend.exception.ResourceNotFoundException;
import com.codenavi.backend.dto.*;
import com.codenavi.backend.exception.CodeRuntimeException;
import com.codenavi.backend.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final Judge0Client judge0Client; // Judge0 클라이언트 의존성 활성화

    /**
     * [신규 추가] 사용자의 코드를 문제의 공개 테스트 케이스에 대해 실행하고 결과를 반환합니다.
     */
    public List<CodeExecutionDto.Response> runCode(String problemNumber, CodeExecutionDto.Request request) {
        Problem problem = problemRepository.findByNumber(problemNumber)
                .orElseThrow(() -> new ResourceNotFoundException("해당 번호의 문제를 찾을 수 없습니다."));

        List<TestCase> publicTestCases = problem.getTestCases().stream()
                .filter(TestCase::isPublic)
                .collect(Collectors.toList());

        int languageId = mapLanguageToJudge0Id(request.getLanguage());

        return IntStream.range(0, publicTestCases.size())
                .mapToObj(i -> {
                    TestCase tc = publicTestCases.get(i);
                    try {
                        Judge0Client.Judge0Result result = judge0Client.execute(languageId, request.getCode(), tc.getInput());

                        String actualOutput = result.getStdout() != null ? result.getStdout().trim() : "";
                        String expectedOutput = tc.getExpectedOutput().trim();
                        boolean isCorrect = actualOutput.equals(expectedOutput);

                        return CodeExecutionDto.Response.builder()
                                .caseNumber(i + 1)
                                .input(tc.getInput())
                                .expectedOutput(tc.getExpectedOutput())
                                .actualOutput(actualOutput)
                                .isCorrect(isCorrect)
                                .executionTime(result.getTime())
                                .memoryUsed(result.getMemory())
                                .build();
                    } catch (CodeRuntimeException e) {
                        throw new CodeRuntimeException(e.getRuntimeErrorMessage(), i + 1, tc.getInput());
                    }
                })
                .collect(Collectors.toList());
    }

    public ProblemDetailDto getProblemDetail(String problemNumber) {
        Problem problem = problemRepository.findByNumber(problemNumber)
                .orElseThrow(() -> new ResourceNotFoundException("해당 번호의 문제를 찾을 수 없습니다."));

        return ProblemDetailDto.from(problem);
    }


    public ProblemDetailDto getProblemDetail(String problemNumber) {
        Problem problem = problemRepository.findByNumber(problemNumber)
                .orElseThrow(() -> new ResourceNotFoundException("해당 번호의 문제를 찾을 수 없습니다."));

        return ProblemDetailDto.from(problem);
    }

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

    public AiEditorialDto getProblemEditorial(String problemNumber) {
        Problem problem = problemRepository.findByNumber(problemNumber)
                .orElseThrow(() -> new ResourceNotFoundException("해당 번호의 문제를 찾을 수 없습니다."));

        return AiEditorialDto.from(problem);
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
