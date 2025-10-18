package com.codenavi.backend.exception;

import lombok.Getter;

/**
 * 코드 실행 중 발생하는 예외의 부모 클래스입니다.
 */
@Getter
public class CodeExecutionException extends RuntimeException {
    public CodeExecutionException(String message) {
        super(message);
    }
}
