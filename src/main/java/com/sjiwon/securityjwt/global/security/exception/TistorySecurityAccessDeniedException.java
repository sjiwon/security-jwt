package com.sjiwon.securityjwt.global.security.exception;

import com.sjiwon.securityjwt.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class TistorySecurityAccessDeniedException extends AccessDeniedException {
    private final ErrorCode code;

    protected TistorySecurityAccessDeniedException(final ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static TistorySecurityAccessDeniedException type(final ErrorCode code) {
        return new TistorySecurityAccessDeniedException(code);
    }
}
