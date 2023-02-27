package com.sjiwon.tistory.security.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestApiExceptionHandler {
    @ExceptionHandler(TistorySecurityException.class)
    public ResponseEntity<ErrorResponse> tistorySecurityException(TistorySecurityException exception) {
        ErrorCode code = exception.getCode();
        loggingException(code);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private void loggingException(ErrorCode code) {
        log.info("statusCode={} || errorCode={} || message={}",
                code.getStatus().value(), code.getErrorCode(), code.getMessage());
    }
}
