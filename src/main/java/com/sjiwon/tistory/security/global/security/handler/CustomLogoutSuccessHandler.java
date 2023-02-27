package com.sjiwon.tistory.security.global.security.handler;

import com.sjiwon.tistory.security.global.security.exception.AuthErrorCode;
import com.sjiwon.tistory.security.global.security.exception.TistorySecurityAccessDeniedException;
import com.sjiwon.tistory.security.token.service.TokenPersistenceService;
import com.sjiwon.tistory.security.token.utils.AuthorizationExtractor;
import com.sjiwon.tistory.security.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenPersistenceService tokenPersistenceService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        removeRefreshToken(request);
        clearSecurityContextHolder();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }

    private void removeRefreshToken(HttpServletRequest request) {
        String accessToken = AuthorizationExtractor.extractToken(request);
        validateAccessToken(accessToken);

        Long memberId = jwtTokenProvider.getId(accessToken);
        tokenPersistenceService.deleteRefreshTokenViaUserId(memberId);
    }

    private void validateAccessToken(String accesstoken) {
        if (accesstoken == null) {
            throw TistorySecurityAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private void clearSecurityContextHolder() {
        SecurityContextHolder.clearContext();
    }
}
