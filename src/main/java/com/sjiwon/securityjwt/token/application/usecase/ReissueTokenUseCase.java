package com.sjiwon.securityjwt.token.application.usecase;

import com.sjiwon.securityjwt.global.exception.CommonException;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.token.application.usecase.command.ReissueTokenCommand;
import com.sjiwon.securityjwt.token.domain.model.AuthToken;
import com.sjiwon.securityjwt.token.domain.service.TokenIssuer;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {
    private final TokenProvider tokenProvider;
    private final TokenIssuer tokenIssuer;

    public AuthToken invoke(final ReissueTokenCommand command) {
        final Long userId = tokenProvider.getId(command.refreshToken());
        validateUserToken(userId, command.refreshToken());
        return tokenIssuer.reissueAuthorityToken(userId);
    }

    private void validateUserToken(final Long userId, final String refreshToken) {
        if (isAnonymousRefreshToken(userId, refreshToken)) {
            throw CommonException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private boolean isAnonymousRefreshToken(final Long userId, final String refreshToken) {
        return !tokenIssuer.isUserRefreshToken(userId, refreshToken);
    }
}
