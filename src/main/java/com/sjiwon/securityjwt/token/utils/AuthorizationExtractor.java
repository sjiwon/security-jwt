package com.sjiwon.securityjwt.token.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

import static com.sjiwon.securityjwt.token.utils.TokenResponseWriter.AUTHORIZATION_HEADER_TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {
    public static Optional<String> extractAccessToken(final HttpServletRequest request) {
        final String token = request.getHeader(AUTHORIZATION);
        if (isEmptyToken(token)) {
            return Optional.empty();
        }
        return checkToken(token.split(" "));
    }

    public static Optional<String> extractRefreshToken(final HttpServletRequest request) {
        final String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(TokenResponseWriter.REFRESH_TOKEN_COOKIE))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (isEmptyToken(token)) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    private static boolean isEmptyToken(final String token) {
        return !StringUtils.hasText(token);
    }

    private static Optional<String> checkToken(final String[] parts) {
        if (parts.length == 2 && parts[0].equals(AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
            return Optional.ofNullable(parts[1]);
        }
        return Optional.empty();
    }
}
