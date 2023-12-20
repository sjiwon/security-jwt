package com.sjiwon.securityjwt.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.global.exception.ErrorResponse;
import com.sjiwon.securityjwt.user.exception.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

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
        if (exception instanceof UsernameNotFoundException) {
            return ErrorResponse.from(UserErrorCode.USER_NOT_FOUND);
        } else {
            return ErrorResponse.from(UserErrorCode.INVALID_PASSWORD);
        }
    }

    private void sendResponse(final HttpServletResponse response, final ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
