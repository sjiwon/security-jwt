package com.sjiwon.securityjwt.global.security.exception;

import com.sjiwon.securityjwt.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class SecurityJwtAuthenticationException extends AuthenticationException {
    private final ErrorCode code;

    private SecurityJwtAuthenticationException(final ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static SecurityJwtAuthenticationException type(final ErrorCode code) {
        return new SecurityJwtAuthenticationException(code);
    }
}
