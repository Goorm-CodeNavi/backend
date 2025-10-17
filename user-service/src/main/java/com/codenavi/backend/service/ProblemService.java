package com.codenavi.backend.service;

import com.codenavi.backend.domain.Problem;
import com.codenavi.backend.domain.TestCase;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.*;
import com.codenavi.backend.exception.CodeCompilationException;
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
    public List<CodeExecutionDto.Response> runCode(String problemNumber, CodeExecutionDto.Request request) {
        Problem problem = problemRepository.findByNumber(problemNumber)
                .orElseThrow(() -> new ResourceNotFoundException("해당 번호의 문제를 찾을 수 없습니다."));

        // --- 외부 코드 실행 서비스 연동 시뮬레이션 ---

        // 1. 컴파일 에러 시뮬레이션: 코드에 "COMPILE_ERROR"가 포함된 경우
        if (request.getCode().contains("COMPILE_ERROR")) {
            String errorMessage = "Main.java:3: error: ';' expected\n    System.out.println(a + b)\n                           ^\n1 error";
            throw new CodeCompilationException("코드 컴파일에 실패했습니다.", errorMessage);
        }

        // 2. 정상 실행 시뮬레이션
        List<TestCase> publicTestCases = problem.getTestCases().stream()
                .filter(TestCase::isPublic)
                .collect(Collectors.toList());

        return IntStream.range(0, publicTestCases.size())
                .mapToObj(i -> {
                    TestCase tc = publicTestCases.get(i);
                    // 실제로는 외부 채점 엔진의 결과를 바탕으로 이 DTO를 채웁니다.
                    return CodeExecutionDto.Response.builder()
                            .caseNumber(i + 1)
                            .input(tc.getInput())
                            .expectedOutput(tc.getExpectedOutput())
                            .actualOutput(tc.getExpectedOutput()) // 정답이라고 가정
                            .isCorrect(true)
                            .build();
                })
                .collect(Collectors.toList());
    }
}