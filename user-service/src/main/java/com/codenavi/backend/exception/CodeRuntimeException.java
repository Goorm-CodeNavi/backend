package com.codenavi.backend.exception;

import lombok.Getter;

@Getter
public class CodeRuntimeException extends CodeExecutionException {
    private final int failedCaseNumber;
    private final String input;
    private final String runtimeErrorMessage;

    public CodeRuntimeException(String runtimeErrorMessage, int failedCaseNumber, String input) {
        super("코드 실행 중 런타임 에러가 발생했습니다.");
        this.runtimeErrorMessage = runtimeErrorMessage;
        this.failedCaseNumber = failedCaseNumber;
        this.input = input;
    }
}
