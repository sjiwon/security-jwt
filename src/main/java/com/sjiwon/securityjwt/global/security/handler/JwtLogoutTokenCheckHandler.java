package com.sjiwon.securityjwt.global.security.handler;

import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.global.security.exception.SecurityJwtAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class JwtLogoutTokenCheckHandler implements LogoutHandler {
    @Override
    public void logout(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) {
        if (authentication == null) {
            throw SecurityJwtAccessDeniedException.type(AuthErrorCode.INVALID_PERMISSION);
        }
    }
}
