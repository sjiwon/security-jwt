package com.sjiwon.securityjwt.global.security.handler;

import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.token.domain.service.TokenManager;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import com.sjiwon.securityjwt.token.utils.TokenResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final TokenManager tokenManager;
    private final TokenResponseWriter tokenResponseWriter;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) {
        final UserPrincipal user = getPrincipal(authentication);
        final String accessToken = tokenProvider.createAccessToken(user.id());
        final String refreshToken = tokenProvider.createRefreshToken(user.id());

        tokenManager.synchronizeRefreshToken(user.id(), refreshToken);
        sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);
    }

    private UserPrincipal getPrincipal(final Authentication authentication) {
        return (UserPrincipal) authentication.getPrincipal();
    }

    private void sendAccessTokenAndRefreshToken(
            final HttpServletResponse response,
            final String accessToken,
            final String refreshToken
    ) {
        tokenResponseWriter.applyAccessToken(response, accessToken);
        tokenResponseWriter.applyRefreshToken(response, refreshToken);
    }
}
