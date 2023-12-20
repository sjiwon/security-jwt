package com.sjiwon.securityjwt.global.annotation;

import com.sjiwon.securityjwt.global.exception.SecurityJwtCommonException;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.token.utils.AuthorizationExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ExtractTokenArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExtractToken.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final String token = AuthorizationExtractor.extractToken(request);
        validateToken(token);
        return token;
    }

    private void validateToken(final String token) {
        if (token == null) {
            throw SecurityJwtCommonException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }
}