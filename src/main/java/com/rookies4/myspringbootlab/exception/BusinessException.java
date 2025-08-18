package com.rookies4.myspringbootlab.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final HttpStatus httpStatus;

    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);                // ✅ 표준 예외 메시지에 세팅
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message) {
        this(HttpStatus.EXPECTATION_FAILED, message);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
