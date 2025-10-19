package com.codenavi.backend.client;

import com.codenavi.backend.exception.CodeCompilationException;
import com.codenavi.backend.exception.CodeRuntimeException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * RapidAPI를 통해 외부 Judge0 API와 실제 HTTP 통신을 담당하는 클라이언트입니다.
 */
@Component
public class Judge0Client {

    private final RestTemplate restTemplate;
    private final String judge0ApiUrl;
    private final String rapidApiKey;
    private final ObjectMapper objectMapper; // JSON 변환을 위해 ObjectMapper 주입

    public Judge0Client(RestTemplate restTemplate,
                        @Value("${judge0.api.url}") String judge0ApiUrl,
                        @Value("${judge0.api.key}") String rapidApiKey) {
        this.restTemplate = restTemplate;
        this.judge0ApiUrl = judge0ApiUrl;
        this.rapidApiKey = rapidApiKey;
        this.objectMapper = new ObjectMapper(); // ObjectMapper 인스턴스 생성
    }

    public Judge0Result execute(int languageId, String sourceCode, String stdin) {
        String submissionToken = createSubmission(languageId, sourceCode, stdin);

        Judge0SubmissionResponse finalResult;
        while (true) {
            finalResult = getSubmissionResult(submissionToken);
            if (finalResult.getStatus() != null && finalResult.getStatus().getId() > 2) {
                break;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling was interrupted", e);
            }
        }

        return processFinalResult(finalResult, stdin);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", rapidApiKey);
        headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String createSubmission(int languageId, String sourceCode, String stdin) {
        String url = judge0ApiUrl + "/submissions?base64_encoded=false&wait=false";

        // 1. Map으로 요청 데이터 구성
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("language_id", languageId);
        requestMap.put("source_code", sourceCode);
        requestMap.put("stdin", stdin);

        try {
            // --- 👇 수정된 부분: Map을 JSON 문자열로 직접 변환합니다. ---
            String requestBody = objectMapper.writeValueAsString(requestMap);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, createHeaders());
            // -----------------------------------------------------------------

            ResponseEntity<Judge0TokenResponse> response = restTemplate.postForEntity(url, entity, Judge0TokenResponse.class);

            if (response.getBody() == null || response.getBody().getToken() == null) {
                throw new RuntimeException("Failed to get submission token from Judge0");
            }
            return response.getBody().getToken();

        } catch (JsonProcessingException e) {
            // JSON 변환 실패 시 런타임 예외 발생
            throw new RuntimeException("Failed to serialize request body to JSON", e);
        }
    }

    private Judge0SubmissionResponse getSubmissionResult(String token) {
        String url = judge0ApiUrl + "/submissions/" + token + "?base64_encoded=false";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Judge0SubmissionResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Judge0SubmissionResponse.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to get submission result from Judge0 for token: " + token);
        }
        return response.getBody();
    }

    private Judge0Result processFinalResult(Judge0SubmissionResponse result, String stdin) {
        if (result.getStatus() == null) {
            throw new RuntimeException("Judge0 returned a result with no status information.");
        }
        int statusId = result.getStatus().getId();

        switch (statusId) {
            case 3:
            case 4:
                return new Judge0Result(result.getStdout(), result.getTime(), result.getMemory());
            case 5:
                throw new CodeRuntimeException("Time Limit Exceeded", 0, stdin);
            case 6:
                throw new CodeCompilationException("코드 컴파일에 실패했습니다.", result.getCompileOutput());
            default:
                String errorMessage = result.getStatus().getDescription() + "\n" + (result.getStderr() != null ? result.getStderr() : "");
                throw new CodeRuntimeException(errorMessage, 0, stdin);
        }
    }

    // --- Judge0 API 통신을 위한 내부 DTO 클래스들 ---

    @Getter @Setter @NoArgsConstructor
    private static class Judge0TokenResponse {
        private String token;
    }

    @Getter @Setter @NoArgsConstructor
    private static class Judge0SubmissionResponse {
        private String stdout;
        private String stderr;

        @JsonProperty("compile_output")
        private String compileOutput;

        private Double time;
        private Double memory;
        private Judge0Status status;
    }

    @Getter @Setter @NoArgsConstructor
    private static class Judge0Status {
        private int id;
        private String description;
    }

    @Getter @Setter
    public static class Judge0Result {
        private String stdout;
        private Double time;
        private Double memory;

        public Judge0Result(String stdout, Double time, Double memory) {
            this.stdout = stdout;
            this.time = time;
            this.memory = memory;
        }
    }
}

