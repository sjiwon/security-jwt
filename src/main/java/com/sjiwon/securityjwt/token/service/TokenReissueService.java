package com.sjiwon.securityjwt.token.service;

import com.sjiwon.securityjwt.global.exception.SecurityJwtCommonException;
import com.sjiwon.securityjwt.global.security.TokenResponse;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenReissueService {
    private final TokenPersistenceService tokenPersistenceService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse reissueTokens(final Long userId, final String refreshToken) {
        // 사용자가 보유하고 있는 Refresh Token인지
        if (!tokenPersistenceService.isRefreshTokenExists(userId, refreshToken)) {
            throw SecurityJwtCommonException.type(AuthErrorCode.EXPIRED_OR_POLLUTED_TOKEN);
        }

        // Access Token & Refresh Token 발급
        final String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        final String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // RTR 정책에 의해 memberId에 해당하는 사용자가 보유하고 있는 Refresh Token 업데이트
        tokenPersistenceService.reissueRefreshTokenByRtrPolicy(userId, newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
