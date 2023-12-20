package com.sjiwon.securityjwt.global.security.handler;

import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.global.security.exception.TistorySecurityAccessDeniedException;
import com.sjiwon.securityjwt.token.service.TokenPersistenceService;
import com.sjiwon.securityjwt.token.utils.AuthorizationExtractor;
import com.sjiwon.securityjwt.token.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenPersistenceService tokenPersistenceService;

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        removeRefreshToken(request);
        clearSecurityContextHolder();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }

    private void removeRefreshToken(final HttpServletRequest request) {
        final String accessToken = AuthorizationExtractor.extractToken(request);
        validateAccessToken(accessToken);

        final Long memberId = jwtTokenProvider.getId(accessToken);
        tokenPersistenceService.deleteRefreshTokenViaUserId(memberId);
    }

    private void validateAccessToken(final String accesstoken) {
        if (accesstoken == null) {
            throw TistorySecurityAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private void clearSecurityContextHolder() {
        SecurityContextHolder.clearContext();
    }
}
