package com.sjiwon.securityjwt.global.annotation;

import com.sjiwon.securityjwt.global.exception.CommonException;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.token.domain.model.Authenticated;
import com.sjiwon.securityjwt.token.utils.RequestTokenExtractor;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {
    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final String accessToken = getAccessToken(request);
        final Long userId = tokenProvider.getId(accessToken);
        return new Authenticated(userId, accessToken);
    }

    private String getAccessToken(final HttpServletRequest request) {
        return RequestTokenExtractor.extractAccessToken(request)
                .orElseThrow(() -> CommonException.type(AuthErrorCode.INVALID_PERMISSION));
    }
}
