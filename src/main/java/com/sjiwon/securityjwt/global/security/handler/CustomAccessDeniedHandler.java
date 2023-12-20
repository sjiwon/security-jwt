package com.sjiwon.securityjwt.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.global.exception.ErrorResponse;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.global.security.exception.TistorySecurityAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        if (accessDeniedException instanceof final TistorySecurityAccessDeniedException ex) {
            objectMapper.writeValue(response.getWriter(), ErrorResponse.from(ex.getCode()));
        } else {
            objectMapper.writeValue(response.getWriter(), ErrorResponse.from(AuthErrorCode.INVALID_TOKEN));
        }
    }
}