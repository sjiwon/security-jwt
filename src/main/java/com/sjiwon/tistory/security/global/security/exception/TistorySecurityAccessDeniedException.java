package com.sjiwon.tistory.security.global.security.exception;

import com.sjiwon.tistory.security.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class TistorySecurityAccessDeniedException extends AccessDeniedException {
    private final ErrorCode code;

    protected TistorySecurityAccessDeniedException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static TistorySecurityAccessDeniedException type(ErrorCode code) {
        return new TistorySecurityAccessDeniedException(code);
    }
}
