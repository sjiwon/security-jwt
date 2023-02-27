package com.sjiwon.tistory.security.global.exception;

import lombok.Getter;

@Getter
public class TistorySecurityException extends RuntimeException {
    private final ErrorCode code;

    public TistorySecurityException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static TistorySecurityException type(ErrorCode code) {
        return new TistorySecurityException(code);
    }
}
