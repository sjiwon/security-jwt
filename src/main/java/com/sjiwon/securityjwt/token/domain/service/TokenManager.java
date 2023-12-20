package com.sjiwon.securityjwt.token.domain.service;

import com.sjiwon.securityjwt.token.domain.model.Token;
import com.sjiwon.securityjwt.token.domain.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;

    @Transactional
    public void synchronizeRefreshToken(final Long userId, final String refreshToken) {
        tokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.issueRefreshToken(userId, refreshToken))
                );
    }

    public void updateRefreshToken(final Long userId, final String newRefreshToken) {
        tokenRepository.updateRefreshToken(userId, newRefreshToken);
    }

    public void deleteRefreshToken(final Long userId) {
        tokenRepository.deleteRefreshToken(userId);
    }

    public boolean isUserRefreshToken(final Long userId, final String refreshToken) {
        return tokenRepository.existsByUserIdAndRefreshToken(userId, refreshToken);
    }
}
