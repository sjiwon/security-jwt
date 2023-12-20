package com.sjiwon.securityjwt.token.exception;

import com.sjiwon.securityjwt.global.exception.CommonException;
import com.sjiwon.securityjwt.global.exception.ErrorCode;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;

public class InvalidTokenException extends CommonException {
    private static final ErrorCode code = AuthErrorCode.INVALID_TOKEN;

    public InvalidTokenException() {
        super(code);
    }
}
