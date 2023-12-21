package com.sjiwon.securityjwt.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.global.security.dto.LoginResponse;
import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.token.domain.model.AuthToken;
import com.sjiwon.securityjwt.token.domain.service.TokenIssuer;
import com.sjiwon.securityjwt.token.utils.TokenResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenIssuer tokenIssuer;
    private final TokenResponseWriter tokenResponseWriter;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException {
        final UserPrincipal user = getPrincipal(authentication);
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(user.id());
        sendAccessTokenAndRefreshToken(response, user, authToken);
    }

    private UserPrincipal getPrincipal(final Authentication authentication) {
        return (UserPrincipal) authentication.getPrincipal();
    }

    private void sendAccessTokenAndRefreshToken(
            final HttpServletResponse response,
            final UserPrincipal user,
            final AuthToken authToken
    ) throws IOException {
        tokenResponseWriter.applyAccessToken(response, authToken.accessToken());
        tokenResponseWriter.applyRefreshToken(response, authToken.refreshToken());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new LoginResponse(user.id(), user.name()));
    }
}
