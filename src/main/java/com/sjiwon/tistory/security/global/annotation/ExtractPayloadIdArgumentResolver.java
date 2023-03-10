package com.sjiwon.tistory.security.global.annotation;

import com.sjiwon.tistory.security.global.exception.TistorySecurityException;
import com.sjiwon.tistory.security.global.security.exception.AuthErrorCode;
import com.sjiwon.tistory.security.token.utils.AuthorizationExtractor;
import com.sjiwon.tistory.security.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class ExtractPayloadIdArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExtractPayloadId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = AuthorizationExtractor.extractToken(request);
        validateToken(token);
        return jwtTokenProvider.getId(token);
    }

    private void validateToken(String token) {
        if (token == null) {
            throw TistorySecurityException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }
}
