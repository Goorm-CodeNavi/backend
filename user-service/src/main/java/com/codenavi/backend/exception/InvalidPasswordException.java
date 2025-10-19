package com.codenavi.backend.exception;

/**
 * 비밀번호가 유효하지 않을 때 (예: 현재 비밀번호 불일치) 발생하는 예외입니다.
 * 이 예외는 컨트롤러에서 403 Forbidden 상태 코드로 처리될 수 있습니다.
 */
public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}
