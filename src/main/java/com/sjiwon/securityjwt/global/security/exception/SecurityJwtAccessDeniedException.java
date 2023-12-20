package com.sjiwon.securityjwt.global.security.exception;

import com.sjiwon.securityjwt.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class SecurityJwtAccessDeniedException extends AccessDeniedException {
    private final ErrorCode code;

    private SecurityJwtAccessDeniedException(final ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static SecurityJwtAccessDeniedException type(final ErrorCode code) {
        return new SecurityJwtAccessDeniedException(code);
    }
}
