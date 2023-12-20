package com.sjiwon.securityjwt.global.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private final ErrorCode code;

    public CommonException(final ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static CommonException type(final ErrorCode code) {
        return new CommonException(code);
    }
}
