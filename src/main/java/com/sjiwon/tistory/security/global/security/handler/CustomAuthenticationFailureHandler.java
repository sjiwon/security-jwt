package com.sjiwon.tistory.security.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.tistory.security.global.exception.ErrorResponse;
import com.sjiwon.tistory.security.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        ErrorResponse errorResponse = null;
        if (exception instanceof UsernameNotFoundException) {
            errorResponse = ErrorResponse.from(UserErrorCode.USER_NOT_FOUND);
        } else if (exception instanceof BadCredentialsException) {
            errorResponse = ErrorResponse.from(UserErrorCode.INVALID_PASSWORD);
        }

        response.setStatus(errorResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
