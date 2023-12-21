package com.sjiwon.securityjwt.user.exception;

import com.sjiwon.securityjwt.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(NOT_FOUND, "USER_001", "사용자 정보가 존재하지 않습니다."),
    INVALID_PASSWORD(UNAUTHORIZED, "USER_002", "비밀번호가 일치하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
