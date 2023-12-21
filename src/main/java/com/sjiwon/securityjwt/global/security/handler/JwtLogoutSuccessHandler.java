package com.sjiwon.securityjwt.global.security.handler;

import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.token.domain.service.TokenIssuer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {
    private final TokenIssuer tokenIssuer;

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        removeRefreshToken(authentication);
        clearSecurityContextHolder();
        sendResponse(response);
    }

    private void removeRefreshToken(final Authentication authentication) {
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        tokenIssuer.deleteRefreshToken(userPrincipal.id());
    }

    private void clearSecurityContextHolder() {
        SecurityContextHolder.clearContext();
    }

    private void sendResponse(final HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }
}
