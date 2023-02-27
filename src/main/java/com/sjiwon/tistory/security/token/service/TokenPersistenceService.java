package com.sjiwon.tistory.security.token.service;

import com.sjiwon.tistory.security.token.domain.Token;
import com.sjiwon.tistory.security.token.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenPersistenceService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        tokenRepository.save(Token.issueToken(userId, refreshToken));
    }

    @Transactional
    public void reissueRefreshTokenByRtrPolicy(Long userId, String newRefreshToken) {
        tokenRepository.reissueRefreshTokenByRtrPolicy(userId, newRefreshToken);
    }

    @Transactional
    public void deleteRefreshTokenViaUserId(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

    public boolean isRefreshTokenExists(Long userId, String refreshToken) {
        return tokenRepository.existsByUserIdAndRefreshToken(userId, refreshToken);
    }
}
