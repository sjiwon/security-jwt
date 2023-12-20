package com.sjiwon.securityjwt.token.domain.service;

import com.sjiwon.securityjwt.token.domain.model.AuthToken;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuer {
    private final TokenProvider tokenProvider;
    private final TokenManager tokenManager;

    public AuthToken provideAuthorityToken(final Long userId) {
        final String accessToken = tokenProvider.createAccessToken(userId);
        final String refreshToken = tokenProvider.createRefreshToken(userId);
        tokenManager.synchronizeRefreshToken(userId, refreshToken);

        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken reissueAuthorityToken(final Long userId) {
        final String newAccessToken = tokenProvider.createAccessToken(userId);
        final String newRefreshToken = tokenProvider.createRefreshToken(userId);
        tokenManager.updateRefreshToken(userId, newRefreshToken);

        return new AuthToken(newAccessToken, newRefreshToken);
    }

    public boolean isUserRefreshToken(final Long userId, final String refreshToken) {
        return tokenManager.isUserRefreshToken(userId, refreshToken);
    }

    public void deleteRefreshToken(final Long userId) {
        tokenManager.deleteRefreshToken(userId);
    }
}
