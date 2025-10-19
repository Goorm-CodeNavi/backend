package com.codenavi.backend.exception;

import lombok.Getter;

/**
 * 코드 컴파일 에러가 발생했을 때 서비스 계층에서 던지는 예외입니다.
 */
@Getter
public class CodeCompilationException extends RuntimeException {

    private final String compileErrorMessage;

    public CodeCompilationException(String message, String compileErrorMessage) {
        super(message);
        this.compileErrorMessage = compileErrorMessage;
    }
}
