package com.rookies4.myspringbootlab.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionAdvice {
    @ExceptionHandler(BusinessException.class)
    protected ProblemDetail handleBusiness(BusinessException e) {
        ProblemDetail pd = ProblemDetail.forStatus(e.getHttpStatus());
        pd.setTitle(e.getHttpStatus().getReasonPhrase());
        pd.setDetail(e.getMessage());
        pd.setProperty("errorCategory", "Generic");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleBadJson(HttpMessageNotReadableException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", e.getMessage());
        result.put("httpStatus", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(result);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorObject> handleRuntime(RuntimeException e) {
        ErrorObject body = new ErrorObject();
        body.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.setMessage(e.getMessage());
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
