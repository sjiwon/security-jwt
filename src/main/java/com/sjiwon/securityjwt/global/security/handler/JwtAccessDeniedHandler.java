package com.sjiwon.securityjwt.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.global.exception.ErrorResponse;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.global.security.exception.SecurityJwtAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AccessDeniedException accessDeniedException
    ) throws IOException {
        final ErrorResponse errorResponse = createErrorResponse(accessDeniedException);
        sendResponse(response, errorResponse);
    }

    private static ErrorResponse createErrorResponse(final AccessDeniedException exception) {
        if (exception instanceof final SecurityJwtAccessDeniedException ex) {
            return ErrorResponse.from(ex.getCode());
        }
        return ErrorResponse.from(AuthErrorCode.INVALID_TOKEN);
    }

    private void sendResponse(final HttpServletResponse response, final ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
