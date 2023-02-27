package com.sjiwon.tistory.security.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.tistory.security.global.exception.ErrorResponse;
import com.sjiwon.tistory.security.global.security.exception.AuthErrorCode;
import com.sjiwon.tistory.security.global.security.exception.TistorySecurityAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        if (accessDeniedException instanceof TistorySecurityAccessDeniedException ex) {
            objectMapper.writeValue(response.getWriter(), ErrorResponse.from(ex.getCode()));
        } else {
            objectMapper.writeValue(response.getWriter(), ErrorResponse.from(AuthErrorCode.INVALID_TOKEN));
        }
    }
}
