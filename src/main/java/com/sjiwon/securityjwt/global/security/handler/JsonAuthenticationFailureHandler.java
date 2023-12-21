package com.sjiwon.securityjwt.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.global.exception.ErrorResponse;
import com.sjiwon.securityjwt.global.exception.GlobalErrorCode;
import com.sjiwon.securityjwt.global.security.exception.SecurityJwtAuthenticationException;
import com.sjiwon.securityjwt.user.exception.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception
    ) throws IOException {
        final ErrorResponse errorResponse = createErrorResponse(exception);
        sendResponse(response, errorResponse);
    }

    private static ErrorResponse createErrorResponse(final AuthenticationException exception) {
        if (exception instanceof final SecurityJwtAuthenticationException ex) {
            return ErrorResponse.from(ex.getCode());
        } else if (exception instanceof UsernameNotFoundException) {
            return ErrorResponse.from(UserErrorCode.USER_NOT_FOUND);
        } else if (exception instanceof BadCredentialsException) {
            return ErrorResponse.from(UserErrorCode.INVALID_PASSWORD);
        } else {
            log.error("{}", exception, exception);
            return ErrorResponse.from(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendResponse(final HttpServletResponse response, final ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
