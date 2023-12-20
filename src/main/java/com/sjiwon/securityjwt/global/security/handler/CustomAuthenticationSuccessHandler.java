package com.sjiwon.securityjwt.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.global.security.TokenResponse;
import com.sjiwon.securityjwt.global.security.principal.UserAuthenticationDto;
import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.token.service.TokenPersistenceService;
import com.sjiwon.securityjwt.token.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenPersistenceService tokenPersistenceService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        final UserAuthenticationDto user = getPrincipal(authentication);
        final String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        final String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        tokenPersistenceService.saveRefreshToken(user.getId(), refreshToken);
        sendAccessTokenAndRefreshToken(response, user, accessToken, refreshToken);
    }

    private UserAuthenticationDto getPrincipal(final Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

    private void sendAccessTokenAndRefreshToken(final HttpServletResponse response, final UserAuthenticationDto user, final String accessToken, final String refreshToken) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        final TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}
