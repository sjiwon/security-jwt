package com.sjiwon.securityjwt.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.global.security.dto.LoginRequest;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.global.security.exception.SecurityJwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_LOGIN_API_URL = new AntPathRequestMatcher("/api/login", "POST");
    private final ObjectMapper objectMapper;

    public JsonAuthenticationFilter(final ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_API_URL);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws AuthenticationException, IOException {
        validateContentType(request);

        final LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        validateLoginRequestData(loginRequest.loginId(), loginRequest.loginPassword());

        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.loginId(),
                loginRequest.loginPassword()
        );
        return this.getAuthenticationManager().authenticate(token);
    }

    private void validateContentType(final HttpServletRequest request) {
        if (isInvalidContentType(request)) {
            throw SecurityJwtAuthenticationException.type(AuthErrorCode.INVALID_AUTH_CONTENT_TYPE);
        }
    }

    private boolean isInvalidContentType(final HttpServletRequest request) {
        return request.getHeader(CONTENT_TYPE) == null
                || !request.getHeader(CONTENT_TYPE).contains(APPLICATION_JSON_VALUE);
    }

    private void validateLoginRequestData(final String loginId, final String loginPassword) {
        if (isEmpty(loginId) || isEmpty(loginPassword)) {
            throw SecurityJwtAuthenticationException.type(AuthErrorCode.INVALID_AUTH_DATA);
        }
    }

    private boolean isEmpty(final String value) {
        return !StringUtils.hasText(value);
    }
}
