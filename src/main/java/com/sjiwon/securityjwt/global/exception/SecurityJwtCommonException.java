package com.sjiwon.securityjwt.global.exception;

import lombok.Getter;

@Getter
public class SecurityJwtCommonException extends RuntimeException {
    private final ErrorCode code;

    public SecurityJwtCommonException(final ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static SecurityJwtCommonException type(final ErrorCode code) {
        return new SecurityJwtCommonException(code);
    }
}
