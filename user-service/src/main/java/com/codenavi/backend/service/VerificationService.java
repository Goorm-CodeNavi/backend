package com.codenavi.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final StringRedisTemplate redisTemplate;
    private static final String VERIFICATION_CODE_PREFIX = "ID_VERIFY:";

    // 6자리 랜덤 인증번호 생성
    public String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // Redis에 인증번호 저장 (유효시간 5분)
    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(
                VERIFICATION_CODE_PREFIX + email,
                code,
                Duration.ofMinutes(5) // 5분 후 자동 삭제
        );
    }

    // Redis에서 인증번호 조회 및 검증
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + email);
        if (storedCode != null && storedCode.equals(code)) {
            // 검증 성공 시 코드 삭제
            redisTemplate.delete(VERIFICATION_CODE_PREFIX + email);
            return true;
        }
        return false;
    }
}