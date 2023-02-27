package com.sjiwon.tistory.security.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.tistory.security.global.security.TokenResponse;
import com.sjiwon.tistory.security.global.security.principal.UserAuthenticationDto;
import com.sjiwon.tistory.security.global.security.principal.UserPrincipal;
import com.sjiwon.tistory.security.token.service.TokenPersistenceService;
import com.sjiwon.tistory.security.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenPersistenceService tokenPersistenceService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserAuthenticationDto user = getPrincipal(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        tokenPersistenceService.saveRefreshToken(user.getId(), refreshToken);
        sendAccessTokenAndRefreshToken(response, user, accessToken, refreshToken);
    }

    private UserAuthenticationDto getPrincipal(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

    private void sendAccessTokenAndRefreshToken(HttpServletResponse response, UserAuthenticationDto user, String accessToken, String refreshToken) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}
